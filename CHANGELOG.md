# Changelog

All notable changes to this project will be documented in this file.

## Version [1.10.0]

- New: Pluxe payment method.
- Improve: Google pay flow.
- Improve: SDK performance and design.


## Version [1.8.0]

- New: Headless flow to get 3Ds challenge Url.

## Version [1.7.0]

- New: Headless flow to generateOtt and get3DsChallenge.


## Version [1.6.1]

- Fix: Antifraud was blocking generate OTT flow.
- IMPORTANT CHANGES: Now we receive argument ```showPaymentStatus``` on
  functions ```startPaymentLite``` and ```startPayment``` this is to decide if show Yuno's error
  screen when an api request failed. Default value is true so by default the SDK will show Yuno's
  error screen.

## Version [1.6.0]

- New: Mercado Pago antifraud.
- Improve: Click to pay flow.
- Fix: Some bugs.

## Version [1.5.14-HF]
- New: Function to create One Time Token with card information in Enrollment and Payment flow.
- FIX: Crash with location permissions.

## Version [1.5.14]
- Improve: Cards step by step Payments flow and One step.

## Version [1.5.13]
- New: Change Mercado Pago Wallet enrollment flow from webview to deeplink and custom tab.
- New: Fraud engines 3DS and Cybersource.

## Version [1.5.12]
- Improve: Cards Payments flow.
- Important Changes: Change the CardExpiryDate component EditText and TextField ItemView (CVV) into a single CardDataStackView component, This is important if you are using SecureFields feature
```XML 
<com.yuno.payments.features.base.ui.views.CardDataStackView
android:id="@+id/cardDataStackView" />
```

## Version [1.5.11]
- Fix: Discover card validation.

## Version [1.5.0]
- New: Antifraud engines in Cards flow.
- New: Click 2 Pay payment method.
- New: Supported language methods.
- New: SDK Design.
- Improve: Cards Payments flow.
- Improve: Performance.

## Version [1.4.12]
- Improve: Cards Payments flow.

## Version [1.4.11]
- Improve: Cards Enrollment flow.

## Version [1.4.10]
- Improve: Google pay flow.
- Fix: Restore SDK status when app is killed or destroyed by Android Os.

## Version [1.4.8]
- Improve: Google pay flow.

## Version [1.4.7]
- Fix: Enrollment card flow step by step don't show cvv and expiration date fields.

## Version [1.4.6]
- New: Allow **VOUCHER** card payment method.
- Improve: Google pay flow.

## Version [1.4.5]
- Improve: Card step by step flow design and Pix flow.

## Version [1.4.4]
- Improve: 3ds flow for card payment method.

## Version [1.4.3]
- Improve: Card step by step flow field jump with the next button when it is empty.

## Version [1.4.2]
- Improve: Card step by step flow design.

## Version [1.4.1]

- New: Allow **GOOGLE PAY** payment method
- New: Allow **SENCILLITO** payment method

## Version [1.4.0]

- New: Allow **SAFETYPAY** payment method
- New: Allow **EFECTY** payment method
- New: Allow **NEQUI** payment method
- New: Allow **YAPPY** payment method
- New: Now we support installments in card payment method.
- New: anti fraud sessionId generated from your application through startCheckout().
- New: Now you can choose if persist our loader.

## Version [1.3.9]

- Fix: Card flow step by step
- New: Design of loading screen
- New: Payment method Mercado Pago Checkout Pro don't need form anymore

## Version [1.3.8]

- Fix: Customer with null document

## Version [1.3.7]

- Improve: Cards flows improved

## Version [1.3.6]

- Important Changes: Remove the flag to require the cvv on rolled cards, now it's dynamic.
```
data class YunoConfig(
  val cardFlow: CardFormType = CardFormType.ONE_STEP,
  val saveCardEnabled: Boolean = false
  )
```

## Version [1.3.5]

- New: Allow **CASH** payment method
- New: Allow **BANK_TRANSFER** payment method

## Version [1.3.4]

- New: Allow **UNLIMINT** payment method
- New: Allow **PagSeguro** payment method

## Version [1.3.3]

- Fix: Design in Card's step by step form.

## Version [1.3.2]

- New: Allow **Tarjeta Clave** payment method
- New: Allow **Kushki** payment method
- New: Allow **Khipu** payment method
- New: Request CVV code in enrolled Cards by flag at init Yuno SDK method
- New: Timer in OTP Screen to expire payment
- New: Add new view to show only enrollment available payment
  methods ```EnrollmentMethodsListView()```
- Important Changes: New field in YunoConfig data class for request CVV code in cards flow:
```
data class YunoConfig(
  val cardFlow: CardFormType = CardFormType.ONE_STEP,
  val saveCardEnabled: Boolean = false,
  val requestSecurityCode: Boolean = false, //NEW FIELD
  )
```

## Version [1.3.1]

- New: Allow **Paga con Rappi** payment method

## Version [1.3.0]

- New: Allow **Arcus Cash** payment method
- New: Allow **Daviplata** payment method
- New: Allow **CoDi** payment method
- New: Allow **PayValida** payment method
- New: Enable/Disable "Save card" checkbox in card forms by YunoConfig in ```initialize``` method
- Important Changes: YunoConfig data class for customize parameters in ```initialize``` method

## Version [1.2.5]

- Fix: Payment and Enrollment state when user cancel.

## Version [1.2.4]

- New: Allow **SPEI** payment method
- New: Enrollment and Payment Card flow by steps.

## Version [1.2.2]

- New: Allow to initialize the enrollment state callback in ```startEnrollment``` method
- New: Allow to initialize the payment state callback in ```continuePayment``` method
- New: Allow to initialize the OTT callback in ```startPayment``` method

## Version [1.2.1]

- Fix: Typo "Paga con tajeta" in spanish language.
- Fix: Error message on document number field.

## Version [1.2.0]

- Important Changes: Add callback in ```startPayment``` method to avoid  ```onActivityResult```
- Important Changes: Add ```initEnrollment``` method to configure the callback and
  avoid  ```onActivityResult```

## Version [1.1.1]

- Fix: Hide environment tag for production keys

## Version [1.1.0]

- Important Changes: In the startEnrollment method now need to send country code ex: "CO"
- New: Add Enrollment flow for Credit cards.
- New: Add new design for Credit Card form.
- New: add onSelected event for PaymentMethodListView to notify if exist a payment method selected.
- New: Add title to separate enrolled payment methods from unEnrolled payment methods.
- New: Add new view to show only enrolled payment methods ```EnrollmentPaymentMethodListView()```
- New: Add new view to show only unEnrolled payment methods ```UnEnrolledPaymentMethodListView()```
- New: Label added to identify current env
- Fix: Improve validation of expiration date in Credit Card Form
- Remove: Deleted ```testResult``` options

## Version [1.0.11]

- Fix: Improved user experience on enrollment flow after open an external app.

## Version [1.0.10]

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