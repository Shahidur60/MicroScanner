package com.msscanner.msscanner.discovery.instruction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class InstructionInfo {
    private int pos;
    private String opcode;
    private Object instruction;
}
