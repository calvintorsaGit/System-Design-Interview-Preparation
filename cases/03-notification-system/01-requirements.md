# 📋 Step 1: Clarify Requirements

> What are we sending?

---

## Requirements

### Functional
1.  Send notifications via **Email**, **HMS (Push)**, and **SMS**.
2.  Support **Bulk/Broadcast** messages (e.g., "Promotion for all users").
3.  Support **Priority** messages (e.g., "OTP" > "Marketing").
4.  Track delivery status (Sent, Delivered, Read, Failed).

### Non-Functional
1.  **High Reliability**: Messages must not be lost.
2.  **Scalability**: Handle spikes (e.g., 10M pushes in 5 minutes).
3.  **Rate Limiting**: Don't overwhelm 3rd-party providers (SendGrid, Twilio).

---

*Next: [02 - High-Level Design →](./02-high-level-design.md)*
