package com.horizen.cryptolibprovider;

import com.horizen.librustsidechains.FieldElement;
import com.horizen.vrfnative.*;

import java.util.EnumMap;
import java.util.Optional;

import static com.horizen.cryptolibprovider.VrfFunctions.KeyType.PUBLIC;
import static com.horizen.cryptolibprovider.VrfFunctions.KeyType.SECRET;

public class VrfFunctionsImplZendoo implements VrfFunctions {
    //@TODO Seed shall be supported from JNI side
    @Override
    public EnumMap<KeyType, byte[]> generatePublicAndSecretKeys(byte[] seed) {
        VRFKeyPair generated = VRFKeyPair.generate();
        VRFSecretKey secretKey = generated.getSecretKey();
        VRFPublicKey publicKey = generated.getPublicKey();

        EnumMap<KeyType, byte[]> keysMap = new EnumMap<>(KeyType.class);
        keysMap.put(SECRET, secretKey.serializeSecretKey());
        keysMap.put(PUBLIC, publicKey.serializePublicKey());

        secretKey.freeSecretKey();
        publicKey.freePublicKey();

        return keysMap;
    }

    @Override
    public EnumMap<ProofType, byte[]> createProof(byte[] secretKeyBytes, byte[] publicKeyBytes, byte[] message) {
        VRFSecretKey secretKey = VRFSecretKey.deserialize(secretKeyBytes);
        VRFPublicKey publicKey = VRFPublicKey.deserialize(publicKeyBytes, true);

        VRFKeyPair keyPair = new VRFKeyPair(secretKey, publicKey);
        FieldElement fieldElement = FieldElementUtils.messageToFieldElement(message);
        VRFProveResult vrfProofAndVrfOutput = keyPair.prove(fieldElement);
        byte[] vrfProofBytes = vrfProofAndVrfOutput.getVRFProof().serializeProof();
        byte[] vrfOutputBytes = vrfProofAndVrfOutput.getVRFOutput().serializeFieldElement();

        EnumMap<ProofType, byte[]> proofsMap = new EnumMap<>(ProofType.class);
        proofsMap.put(ProofType.VRF_PROOF, vrfProofBytes);
        proofsMap.put(ProofType.VRF_OUTPUT, vrfOutputBytes);

        secretKey.freeSecretKey();
        publicKey.freePublicKey();
        vrfProofAndVrfOutput.getVRFProof().freeProof();
        vrfProofAndVrfOutput.getVRFOutput().freeFieldElement();
        fieldElement.freeFieldElement();

        return proofsMap;
    }

    @Override
    public boolean verifyProof(byte[] message, byte[] publicKeyBytes, byte[] proofBytes) {
        return proofToOutput(publicKeyBytes, message, proofBytes).isPresent();
    }

    @Override
    public boolean publicKeyIsValid(byte[] publicKeyBytes) {
        VRFPublicKey publicKey = VRFPublicKey.deserialize(publicKeyBytes, true);
        boolean keyIsValid = publicKey.verifyKey();
        publicKey.freePublicKey();

        return keyIsValid;
    }

    @Override
    public Optional<byte[]> proofToOutput(byte[] publicKeyBytes, byte[] message, byte[] proofBytes) {
        VRFPublicKey publicKey = VRFPublicKey.deserialize(publicKeyBytes, true);
        VRFProof vrfProof = VRFProof.deserialize(proofBytes, true);
        FieldElement messageAsFieldElement = FieldElementUtils.messageToFieldElement(message);

        FieldElement vrfOutput = publicKey.proofToHash(vrfProof, messageAsFieldElement);

        Optional<byte[]> output;
        if(vrfOutput != null) {
            output = Optional.of(vrfOutput.serializeFieldElement());
            vrfOutput.freeFieldElement();
        }
        else {
            output = Optional.empty();
        }

        publicKey.freePublicKey();
        vrfProof.freeProof();
        messageAsFieldElement.freeFieldElement();

        return output;
    }

    @Override
    public int vrfSecretKeyLength() {
        return VRFSecretKey.SECRET_KEY_LENGTH;
    }

    @Override
    public int vrfPublicKeyLen() {
        return VRFPublicKey.PUBLIC_KEY_LENGTH;
    }

    @Override
    public int vrfProofLen() {
        return VRFProof.PROOF_LENGTH;
    }

    @Override
    public int vrfOutputLen() {
        return FieldElement.FIELD_ELEMENT_LENGTH;
    }
}
