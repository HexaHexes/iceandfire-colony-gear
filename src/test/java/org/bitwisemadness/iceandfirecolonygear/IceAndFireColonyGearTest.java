package org.bitwisemadness.iceandfirecolonygear;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Keeps the junit CI check non-vacuous: the suite must always assert something
 * real, so the check cannot pass by running zero tests.
 */
class IceAndFireColonyGearTest {
    @Test
    void modIdIsPlainLowercaseIdentifier() {
        assertTrue(IceAndFireColonyGear.MOD_ID.matches("[a-z][a-z0-9_]*"),
                "MOD_ID must be a plain lowercase identifier, got: " + IceAndFireColonyGear.MOD_ID);
    }
}
