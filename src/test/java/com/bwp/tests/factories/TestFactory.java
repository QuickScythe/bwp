package com.bwp.tests.factories;

import com.bwp.Main;
import com.quiptmc.core.QuiptIntegration;

import java.io.File;

public class TestFactory {

    public static QuiptIntegration integration(){
        return new Main.Integration("data-test");
    }
}
