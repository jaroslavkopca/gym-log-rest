package cz.cvut.fel.ear.posilovna.rest;


import cz.cvut.fel.ear.posilovna.exception.NotFoundException;
import cz.cvut.fel.ear.posilovna.exception.ValidationException;
import cz.cvut.fel.ear.posilovna.model.*;
import cz.cvut.fel.ear.posilovna.rest.facade.MemberStatisticsDTO;
import cz.cvut.fel.ear.posilovna.rest.facade.MemberStatisticsFacadeImpl;
import cz.cvut.fel.ear.posilovna.rest.util.RestUtils;
import cz.cvut.fel.ear.posilovna.security.model.UserDetails;
import cz.cvut.fel.ear.posilovna.service.ClientService;
import cz.cvut.fel.ear.posilovna.service.MemberService;
import cz.cvut.fel.ear.posilovna.service.WorkoutRecordDecoratorService;
import cz.cvut.fel.ear.posilovna.service.WorkoutRecordService;
import org.antlr.v4.runtime.misc.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/member")
public class MemberController {
    private static final Logger LOG = LoggerFactory.getLogger(MemberController.class);
    private final MemberService memberService;
    private final ClientService clientService;
    private final MemberStatisticsFacadeImpl memberFacade;
    private final WorkoutRecordService workoutRecordService;


    @Autowired
    public MemberController(MemberService memberService, ClientService clientService, WorkoutRecordService workoutRecordService, MemberStatisticsFacadeImpl memberFacade) {
        this.memberService = memberService;
        this.memberFacade = memberFacade;
        this.clientService = clientService;
        this.workoutRecordService = workoutRecordService;
    }

    @PreAuthorize("hasRole('ROLE_CLIENT')")
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Client client = userDetails.getUser();

            Member newMember = memberService.createMemberFromClient(client);
            if (newMember != null) {
                // The member was successfully created
                return ResponseEntity.ok(newMember);
//            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/register");
//            return new ResponseEntity<>(headers, HttpStatus.CREATED);
            } else {
                // Handling the case where member creation failed
                return ResponseEntity.badRequest().body("Member could not be created");
            }
        }
        return ResponseEntity.badRequest().body("Not logged in");
    }


    @PostMapping(value = "/createClient", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createClient(@RequestBody Client client) {
        // Check if the client has all the required attributes
        if (client != null && client.getName() != null && client.getSurname() != null) {
            Object authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
                // Proceed with client creation
                Client existingClient = clientService.findByUsername(client.getUsername());
                if (existingClient == null) {
                    // Client doesn't exist, create the client
                    try {
                        Client newClient = clientService.createClient(client);
                        return ResponseEntity.ok(newClient); // Successful creation
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body("Client could not be created");
                    }
                } else {
                    // Client already exists, return a bad request
                    return ResponseEntity.badRequest().body("Client with username " + client.getUsername() + " already exists");
                }
            } else {
                return ResponseEntity.badRequest().body("Already is a client");
            }
        } else {
            // Handling the case where the request body is missing required attributes
            return ResponseEntity.badRequest().body("Client attributes are missing");
        }
    }

    @PostMapping(value = "/deleteClient", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteClient(@RequestParam(name = "clientId") @NotNull Integer clientId) {
        // Check if the client exists
        Client existingClient = clientService.findById(clientId);
        if (existingClient == null) {
            return ResponseEntity.badRequest().body("Client not found with ID: " + clientId);
        }

        // Attempt to delete the client
        try {
            clientService.deleteClient(clientId);
            return ResponseEntity.ok("Client deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete client: " + e.getMessage());
        }
    }


    @PostMapping(value = "/deactivateMembership", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deactivateMembership(@RequestParam(name = "clientId") @NotNull Integer clientId) {
        // Check if the client exists
        Client existingClient = clientService.findById(clientId);
        if (existingClient == null) {
            return ResponseEntity.badRequest().body("Client not found with ID: " + clientId);
        }

        // Deactivate the client's membership
        try {
            memberService.deactivateMembership(existingClient.getMember());
            return ResponseEntity.ok("Membership deactivated successfully for client with ID: " + clientId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to deactivate membership: " + e.getMessage());
        }
    }

    @GetMapping("/getClientMemberships")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Membership>> getClientMemberships(@RequestParam(name = "clientId") @NotNull Integer clientId) {
        List<Membership> memberships = memberService.getMembershipsByClientIdOrderByNewest(clientId);

        if (memberships.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(memberships);
        }
    }


    @PostMapping(value = "/payMembership", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_MEMBER')")
    public ResponseEntity<?> payMembership() {
        // Get the currently authenticated user's authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Client client = userDetails.getUser();
            if (client.getRole() == Role.CLIENT) {
                registerClient();
                memberService.activateMembership(client.getMember());
                return ResponseEntity.ok("Membership activated for client");
            } else if (client.getRole() == Role.MEMBER) {
                boolean result = false;
                try {
                    result = memberService.activateMembership(client.getMember());
                    return ResponseEntity.ok("Membership activated for registered client");
                } catch (Exception e) {
                    return ResponseEntity.ok("idkdqwwd" + result + e.getMessage());
                }
            }
        }
        return null;
    }


    @PreAuthorize("hasAnyRole('ROLE_CLIENT', 'ROLE_MEMBER', 'ROLE_MEMBER_WITH_MEMBERSHIP','ROLE_ADMIN')")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public Client getCurrent(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        return ((UserDetails) auth.getPrincipal()).getUser();
    }


    // Handles workout record

    @PreAuthorize("hasAnyRole('ROLE_MEMBER_WITH_MEMBERSHIP')")
    @PostMapping(value = "{id}/workoutrecords", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addWorkoutRecordToMember(@PathVariable Long id, @RequestBody WorkoutRecord workoutRecord) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Client client = userDetails.getUser();
            if (client.getMember().getId() != id.intValue()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                final Member member = memberService.getMemberById(id);
                memberService.addWorkoutRecord(member, workoutRecord);
                LOG.debug("Workout record {} added to member {}.", workoutRecord, member);
                final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", workoutRecord.getId());
                return new ResponseEntity<>(headers, HttpStatus.CREATED);
            }
        }
        return null;
    }

    @PreAuthorize("hasAnyRole('ROLE_MEMBER_WITH_MEMBERSHIP')")
    @GetMapping(value = "/{id}/workoutrecords", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WorkoutRecord>> getWorkoutrecordsByMember(@PathVariable Long id ,@RequestParam(name = "deleted", required = false) Boolean deleted) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Client client = userDetails.getUser();
            if (client.getMember().getId() != id.intValue()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            } else {
                if (deleted != null){
                   // workoutRecordDecoratorService // jako dependency injection bude workoutRecordService - musi byt o metodu navic
                    List<WorkoutRecord> workoutrecords = new WorkoutRecordDecoratorService(this.workoutRecordService).findAllDeleted(memberService.getMemberById(id));
                    return ResponseEntity.ok(workoutrecords);
                }
                else{
                    List<WorkoutRecord> workoutrecords = workoutRecordService.findAll(memberService.getMemberById(id));
                    return ResponseEntity.ok(workoutrecords);
                }
            }

        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    }

    @PreAuthorize("hasAnyRole('ROLE_MEMBER_WITH_MEMBERSHIP')")
    @GetMapping(value = "/{id}/workoutrecords/{idRecord}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getWorkoutRecord(@PathVariable Integer id,@PathVariable Integer idRecord) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WorkoutRecord r = null;
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Client client = userDetails.getUser();
            if (client.getMember().getId() != id.intValue()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } else {
                r = workoutRecordService.findById(memberService.getMemberById(Long.valueOf(id)), idRecord);
                if (r!=null){
                    return ResponseEntity.ok(r);
                }
                else {
                    throw NotFoundException.create("Workout record", idRecord);

                }
            }
        }
        return ResponseEntity.notFound().build();
    }


    @PreAuthorize("hasRole('ROLE_MEMBER_WITH_MEMBERSHIP')")
    @PutMapping(value = "/{id}/workoutrecords/{idRecord}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateWorkoutRecord(@PathVariable Integer id, @PathVariable Integer idRecord, @RequestBody WorkoutRecord workoutRecord) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WorkoutRecord r = null;
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Client client = userDetails.getUser();
            if (client.getMember().getId() != id.intValue()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } else {
                final Member member = memberService.getMemberById(Long.valueOf(id));
                WorkoutRecord original = workoutRecordService.findById(member, idRecord);
                if (!original.getId().equals(workoutRecord.getId())) {
                    throw new ValidationException("Workout record identifier in the data does not match the one in the request URL.");
                }
                workoutRecordService.update(member, workoutRecord);
                return ResponseEntity.noContent().build();
            }
        }
        return null;
    }

    @PreAuthorize("hasRole('ROLE_MEMBER_WITH_MEMBERSHIP')")
    @DeleteMapping(value = "/{id}/workoutrecords/{idRecord}")
    public ResponseEntity<?> removeProduct(@PathVariable Integer id,@PathVariable Integer idRecord) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        WorkoutRecord r = null;
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Client client = userDetails.getUser();
            if (client.getMember().getId() != id.intValue()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } else {
                r = workoutRecordService.findById(memberService.getMemberById(Long.valueOf(id)), idRecord);
                if (r!=null){
                    workoutRecordService.remove(r);
                    return ResponseEntity.noContent().build();
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/statistics")
    public ResponseEntity<List<MemberStatisticsDTO>> getAllMembersAggregatedData() {
        List<MemberStatisticsDTO> aggregatedDataList = memberFacade.getAllMembersAggregatedData();
        return new ResponseEntity<>(aggregatedDataList, HttpStatus.OK);
    }
}
