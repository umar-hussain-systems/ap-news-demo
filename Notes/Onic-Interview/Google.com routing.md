
#### 🔥 One-line interview summary

```  
“The browser resolves DNS, establishes TCP and TLS,
sends an HTTP request, the load balancer routes it to a backend,
the server responds, and the browser parses and renders the page.”

```


1️⃣ Browser checks cache (DNS + HTTP)

DNS cache: Browser → OS → router → ISP
If google.com was resolved recently, reuse the IP.

HTTP cache: If the resource is cached and still valid
(Cache-Control, ETag, If-None-Match) → skip network call or get 304 Not Modified.

👉 Goal: avoid the network if possible.

2️⃣ DNS resolution (recursive → authoritative)

If IP not cached:

Browser asks recursive resolver (ISP / public DNS).

Resolver queries:

Root DNS → .com

TLD DNS → google.com

Authoritative DNS → final IP

IP returned and cached with TTL.

👉 DNS just maps name → IP.

3️⃣ TCP 3-way handshake

Browser opens a TCP connection to the IP:

SYN → “Can I connect?”

SYN-ACK → “Yes”

ACK → “Connected”

👉 Reliable connection established.

4️⃣ TLS handshake (HTTPS cert verification)

For HTTPS:

Server sends SSL certificate

Browser:

Verifies CA chain

Checks domain + expiry

Keys exchanged (public → symmetric)

👉 Secure encrypted channel created.

5️⃣ HTTP request sent

Browser sends:

GET / HTTP/1.1
Host: google.com
Headers (cookies, auth, cache hints)


👉 Now the server actually sees the request.

6️⃣ Load balancer routes request

Load balancer receives request

Routes to backend using:

Round-robin / least-connections

Sticky session (cookie / header)

May terminate TLS or pass through

👉 Scales traffic safely.

7️⃣ Backend processes

Web server → application → database / cache

Business logic runs

Response generated (HTML / JSON)

👉 This is where your Java / Spring code executes.

8️⃣ Response returned

Backend → LB → browser

Includes:

Status code (200 / 304 / 500)

Headers (cache, cookies)

Body (HTML / JSON)

👉 Network journey completes.

9️⃣ Browser renders (HTML → CSS → JS → Paint)

Rendering pipeline:

Parse HTML → DOM

Parse CSS → CSSOM

Combine → Render Tree

Layout → positions

Paint → pixels on screen

JS executes → may trigger reflow/repaint

👉 User sees the page.

