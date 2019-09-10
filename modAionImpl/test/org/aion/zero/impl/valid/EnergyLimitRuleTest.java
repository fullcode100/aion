package org.aion.zero.impl.valid;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.aion.zero.impl.api.BlockConstants;
import org.aion.zero.impl.types.A0BlockHeader;
import org.junit.Test;

/** Tests for {@link EnergyLimitRule} */
public class EnergyLimitRuleTest {

    private final BlockConstants constants = new BlockConstants();

    @Test
    public void testEnergyLimitBounds() {
        final long INITIAL_VAL = 2000000L;
        final long DIVISOR = 1024;
        EnergyLimitRule rule =
                new EnergyLimitRule(
                        constants.getEnergyDivisorLimitLong(), constants.getEnergyLowerBoundLong());

        A0BlockHeader parentHeader =
                A0BlockHeader.Builder.newInstance().withEnergyLimit(INITIAL_VAL).build();

        long boundShiftLimit = INITIAL_VAL / DIVISOR;

        A0BlockHeader upperCurrentBlock =
                A0BlockHeader.Builder.newInstance().withEnergyLimit(INITIAL_VAL + boundShiftLimit).build();

        List<RuleError> errors = new ArrayList<>();

        // upper bound
        boolean res = rule.validate(upperCurrentBlock, parentHeader, errors);
        assertThat(res).isEqualTo(true);
        assertThat(errors).isEmpty();
        errors.clear();

        A0BlockHeader invalidCurrentHeader =
                A0BlockHeader.Builder.newInstance()
                        .withEnergyLimit(INITIAL_VAL + boundShiftLimit + 1)
                        .build();

        res = rule.validate(invalidCurrentHeader, parentHeader, errors);
        assertThat(res).isEqualTo(false);
        assertThat(errors).isNotEmpty();
        errors.clear();

        // lower bound
        A0BlockHeader lowerCurrentHeader =
                A0BlockHeader.Builder.newInstance().withEnergyLimit(INITIAL_VAL - boundShiftLimit).build();

        res = rule.validate(lowerCurrentHeader, parentHeader, errors);
        assertThat(res).isEqualTo(true);
        assertThat(errors).isEmpty();
        errors.clear();

        A0BlockHeader invalidLowerCurrentHeader =
                A0BlockHeader.Builder.newInstance()
                        .withEnergyLimit(INITIAL_VAL - boundShiftLimit - 1)
                        .build();

        res = rule.validate(invalidLowerCurrentHeader, parentHeader, errors);
        assertThat(res).isEqualTo(false);
        assertThat(errors).isNotEmpty();
        errors.clear();
    }

    @Test
    public void testEnergyLimitLowerBound() {
        final long INITIAL_VAL = 0l;

        A0BlockHeader parentHeader = A0BlockHeader.Builder.newInstance().withEnergyLimit(0l).build();

        A0BlockHeader currentHeader = A0BlockHeader.Builder.newInstance().withEnergyLimit(1l).build();

        List<RuleError> errors = new ArrayList<>();

        EnergyLimitRule rule =
                new EnergyLimitRule(
                        constants.getEnergyDivisorLimitLong(), constants.getEnergyLowerBoundLong());
        boolean res = rule.validate(currentHeader, parentHeader, errors);
        assertThat(res).isEqualTo(false);
        assertThat(errors).isNotEmpty();
    }
}
