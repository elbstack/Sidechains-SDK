package com.horizen.proposition;

import scorex.util.serialization.Reader;
import scorex.util.serialization.Writer;

public final class NullPropositionSerializer implements PropositionSerializer<NullProposition> {
    private static NullPropositionSerializer serializer;

    static {
        serializer = new NullPropositionSerializer();
    }

    private NullPropositionSerializer() {
        super();
    }

    public static NullPropositionSerializer getSerializer() {
        return serializer;
    }

    @Override
    public void serialize(NullProposition proposition, Writer writer) {
    }

    @Override
    public NullProposition parse(Reader reader) {
        return new NullProposition();
    }
}
