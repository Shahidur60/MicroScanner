package com.msscanner.msscanner.model.context;

import com.msscanner.msscanner.model.wrongCuts.EntityPair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor
@Data public class WrongCutsContext {
    List<EntityPair> entityCounts;
    List<EntityPair> possibleWrongCuts;
}
