import { RSAKey } from 'jsrsasign';
import { BigInteger } from 'jsbn';
// Helper function to decode base64 to hex
function base64ToHex(base64) {
    const base64UrlSafe = base64.replace(/-/g, '+').replace(/_/g, '/'); // Handle URL-safe base64
    const raw = atob(base64UrlSafe); // Decode base64 string
    return Array.from(raw).map(c => c.charCodeAt(0).toString(16).padStart(2, '0')).join('');
}

// Function to extract modulus and exponent from the RSA public key
function extractModulusAndExponent(publicKeyBase64) {
    // Decode the Base64 string to get the public key in PEM format
    const keyPEM = `-----BEGIN PUBLIC KEY-----\n${publicKeyBase64}\n-----END PUBLIC KEY-----`;

    const rsaKey = new RSAKey();
    rsaKey.readPrivateKeyFromPEMString(keyPEM);  // Correct way to read the public key from PEM string

    const modulus = rsaKey.n.toString(16); // Modulus in hex
    const exponent = rsaKey.e.toString(16); // Exponent in hex

    return {
        modulus,
        exponent
    };
}

// Function to generate a blinding factor (r)
function generateBlindingFactor(n) {
    const one = new BigInteger("1");
    const bitLength = n.bitLength();
    const byteLength = Math.ceil(bitLength / 8);
    const maxAttempts = 50;

    for (let i = 0; i < maxAttempts; i++) {
        const randomBytes = new Uint8Array(byteLength);
        crypto.getRandomValues(randomBytes);

        const hex = Array.from(randomBytes).map(b => b.toString(16).padStart(2, '0')).join('');
        const r = new BigInteger(hex, 16);

        if (r.compareTo(n) < 0 && r.gcd(n).equals(one)) {
            return r;
        }
    }

    throw new Error("Failed to generate blinding factor after multiple attempts");
}

// Function to blind a message using RSA public key
export function blindMessage(message, publicKeyBase64) {
    const { modulus, exponent } = extractModulusAndExponent(publicKeyBase64);

    const e = new BigInteger(exponent, 16);
    const n = new BigInteger(modulus, 16);

    const one = new BigInteger("1");

    const r = generateBlindingFactor(n);
    const m = new BigInteger(message, 16);

    const blindedMessage = m.multiply(r.modPow(e, n)).mod(n);

    return {
        blindedMessage: blindedMessage.toString(16),
        r: r.toString(16),
        n: n.toString(16)
    };
}

// Function to unblind the message
export function unblindMessage(signedBlindedMessage, r, n) {
    const rBigInt = new BigInteger(r, 16);
    const nBigInt = new BigInteger(n, 16);
    const signedBlinded = new BigInteger(signedBlindedMessage, 16);

    const rInv = rBigInt.modInverse(nBigInt);
    const unblindedMessage = signedBlinded.multiply(rInv).mod(nBigInt);

    return unblindedMessage.toString(16); // Return actual voter token
}
