package com.msscanner.msscanner.model.context;

import com.msscanner.msscanner.model.persistency.DatabaseType;
import com.msscanner.msscanner.model.standards.BusinessType;
import com.msscanner.msscanner.model.standards.PresentationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor @AllArgsConstructor
@Data public class TooManyStandardsContext {
    Set<PresentationType> presentationTypes;
    Set<BusinessType> businessTypes;
    Set<DatabaseType> dataTypes;
}
