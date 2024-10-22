package cz.cvut.fel.ear.posilovna.rest;

import cz.cvut.fel.ear.posilovna.model.Client;
import cz.cvut.fel.ear.posilovna.model.GroupWorkout;
import cz.cvut.fel.ear.posilovna.rest.util.GroupWorkoutObject;
import cz.cvut.fel.ear.posilovna.security.SecurityUtils;
import cz.cvut.fel.ear.posilovna.security.model.UserDetails;
import cz.cvut.fel.ear.posilovna.service.ClientService;
import cz.cvut.fel.ear.posilovna.service.GroupWorkoutService;
import cz.cvut.fel.ear.posilovna.service.MemberService;
import cz.cvut.fel.ear.posilovna.rest.util.RestUtils;
import org.antlr.v4.runtime.misc.NotNull;
import org.aspectj.lang.annotation.RequiredTypes;
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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/rest/groupworkout")
public class GroupWorkoutController {
    private final GroupWorkoutService groupWorkoutService;


    @Autowired
    public GroupWorkoutController(GroupWorkoutService groupWorkoutService, ClientService clientService) {
        this.groupWorkoutService = groupWorkoutService;
    }

    @PreAuthorize("hasAnyRole('ROLE_MEMBER_WITH_MEMBERSHIP')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createGroupWorkout(Principal principal, @RequestBody GroupWorkoutObject gwo) {
        final Authentication auth = (Authentication) principal;
        final GroupWorkout newGroupWorkout;
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        if (auth!=null){
            newGroupWorkout = groupWorkoutService.createGroupWorkout(userDetails.getUser().getId(), gwo.getWorkoutName(), gwo.getTimeFrom(), gwo.getTimeTo(), gwo.getCapacity(), gwo.getRoomId());
            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", newGroupWorkout.getId());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        }
        return null;
    }
}
