# Changelog
All notable changes to this project will be documented in this file.

## Version [1.1.1]
- Fix: Hide environment tag for production keys

## New Version [1.1.0]
- Important Changes: In the startEnrollment method now need to send country code ex: "CO"
- New: Add Enrollment flow for Credit cards.
- New: Add new design for Credit Card form.
- New: Add title to separate enrolled payment methods from unEnrolled payment methods.
- New: Add new view to show only enrolled payment methods ```EnrollmentPaymentMethodListView()```
- New: Add new view to show only unEnrolled payment methods ```UnEnrolledPaymentMethodListView()```
- New: Label added to identify current env
- Fix: Improve validation of expiration date in Credit Card Form

## New Version [1.0.11]
- Fix: Improved user experience on enrollment flow after open an external app. 

## New Version [1.0.10]
- Fix: Avoid 404 page after complete enrollment flow.  

## Version [1.0.9] - (2022-09-26)
- Fix: Avoid infinite loader on payment lite flow.

## Version [1.0.8] - (2022-09-21)
- New: Remove Moshi dependency with Gson converter library

## Version [1.0.7] - (2022-09-02)
- New: Change android hint type on attrs with reference.

## Version [1.0.6] - (2022-08-29)
- Fix: Improved pay method item design for full version.
- Fix: Correction phone field, it was shown where it shouldn't be

## Version [1.0.5] - (2022-08-11)
- New: Allow **SafetyPay** payment method
- New: Add webSocket to notify payment state.
- New: Remove user address field from Addi form.
- Fix: CustomerForm regex validators.

## Version [1.0.4] - (2022-08-10)
- New: Change in transaction status when started to pending

## Version [1.0.3] - (2022-08-10)
- New: Add internal error state callback

## Version [1.0.2] - (2022-08-10)
- Fix: Reset cvv regex when delete card number on payment form

## Version [1.0.1] - (2022-07-21)
- New: Bank transfer or card payment type filter is added
- New: Testing features payments view models.
- Fix: Payment type name PIX and Nupay.
- Change: Set time zone to UTC on DateExtension.
- Change: Payment method type on payment mappers.

## Version [1.0.0] - (2022-07-06)
### New Payment Methods
- New: Allow enroll ***Mercado Pago method***.
- New: Allow payments withPay with debit or credit card.
- New: Allow payments with Mercado Pago Checkout Pro.
- Fix: Hint ant the title city text field
- Fix: Change Pix type on payments flow
- Fix: Card information on start checkout
- Fix: Change of translation to English in payment form title 