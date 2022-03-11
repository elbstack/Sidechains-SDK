package com.horizen.proposition;

import com.fasterxml.jackson.annotation.JsonView;
import com.horizen.serialization.Views;

@JsonView(Views.Default.class)
public final class NullProposition implements Proposition {

    @Override
    public PropositionSerializer serializer() {
        return NullPropositionSerializer.getSerializer();
    }
}
