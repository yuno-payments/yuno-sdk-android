# Vocabulary & Glossary

Domain-specific terms used in this project.

| Term | Definition |
|------|-----------|
| OTT (One-Time Token) | A temporary token returned by the SDK after the user completes a payment form. The merchant uses this token to create a payment on their backend via the Yuno API. |
| Checkout Session | A server-generated session ID that identifies a payment transaction. Created via the Yuno API before starting a payment flow. |
| Customer Session | A server-generated session ID that identifies a customer for enrollment operations. Created via the Yuno API before enrolling a payment method. |
| Payment Method Type | A string identifier for the type of payment method (e.g., `CARD`, `PSE`, `PIX`). Used in Lite flows where the merchant controls payment method selection. |
| Vaulted Token | A stored token representing a previously saved payment method. Used to pay with a saved card without re-entering details. |
| Country Code | ISO 3166-1 alpha-2 country code (e.g., `CO`, `BR`, `MX`). Required by the SDK to determine available payment methods and regulatory requirements. |
| Enrollment | The process of saving (vaulting) a payment method for future use, without making a payment. |
| Checkout Lite | Integration pattern where the merchant controls the payment method selection UI and passes the selected method to the SDK via `startPaymentLite()`. |
| Checkout Complete | Integration pattern where the SDK provides its own payment method list UI (`PaymentMethodListViewComponent`) and the merchant calls `startPayment()` to initiate the payment form. |
| Payment Render | Integration pattern where the SDK renders the payment form as a Fragment inside a merchant-provided `FrameLayout` container, giving the merchant full control over the surrounding layout. |
| Enrollment Lite | Integration pattern where the SDK manages the entire enrollment UI. The merchant only calls `startEnrollment()`. |
| Enrollment Render | Integration pattern where the SDK renders the enrollment form as a Fragment inside a merchant-provided `FrameLayout`, similar to Payment Render. |
| Payment Status | A string returned by the SDK indicating the result of a payment: `SUCCEEDED`, `FAIL`, `REJECT`, `CANCELED`, or `PROCESSING`. |
| continuePayment | SDK method called after the merchant creates a payment on their backend using the OTT. It tells the SDK to poll for the final payment status and may trigger additional steps like 3DS verification. |
| needsSubmit | A boolean flag in `YunoEnrollmentRenderListener.showView()` indicating whether the payment method requires the merchant to provide a "Submit" button (e.g., credit card forms require explicit submission). |
| FragmentController | An object (`YunoPaymentFragmentController` or `YunoEnrollmentFragmentController`) returned by Render flows that provides `submitForm()` and `continuePayment()` methods to control the SDK fragment. |
| YunoConfig | Configuration object passed to `Yuno.initialize()` with options like `saveCardEnabled`. |
| PaymentSelected | Data class containing `paymentMethodType` and optional `vaultedToken`, used to specify which payment method to use in Lite and Render flows. |
