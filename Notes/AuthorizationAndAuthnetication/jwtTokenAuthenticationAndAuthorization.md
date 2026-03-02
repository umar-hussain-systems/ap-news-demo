Login 

Validate username/password ✔

Compare encrypted password ✔

Load permissions ✔

Generate JWT ✔

Two small improvements for interview polish later:

Password comparison is done using PasswordEncoder.matches()

JWT should usually be stateless (Redis session is optional depending on design)


validation On every request

Your filter extracts token.

It recomputes/validates the signature using the secret key (symmetric) or public key (asymmetric).

If anyone changed even 1 character in payload (e.g., added ADMIN), the signature check fails, and token is rejected.

So you don’t need Redis to detect tampering. The signature does that.

“JWT integrity is validated via signature (JWS). We don’t need server storage to detect tampering. 
Redis is only needed 
if we want revocation, session tracking, single-session rules, 
or immediate permission invalidation.”