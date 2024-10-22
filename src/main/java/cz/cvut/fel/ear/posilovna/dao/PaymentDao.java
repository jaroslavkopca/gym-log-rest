package cz.cvut.fel.ear.posilovna.dao;

import cz.cvut.fel.ear.posilovna.model.Payment;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentDao extends BaseDao<Payment> {
    public PaymentDao() {super(Payment.class);}
}
