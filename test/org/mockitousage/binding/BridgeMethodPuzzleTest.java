/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.binding;

import static org.mockito.Mockito.*;
import static org.mockito.util.ExtraMatchers.*;

import org.junit.Test;
import org.mockito.TestBase;

/**
 * Bridge method is generated by compiler when erasure in parent class is
 * different. When is different then it means that in runtime we will have
 * overloading rather than overridding Therefore the compiler generates bridge
 * method in Subclass so that erasures are the same, signatures of methods match
 * and overridding is ON.
 */
@SuppressWarnings("unchecked")
public class BridgeMethodPuzzleTest extends TestBase {
    
    private class Super<T> {
        public String say(T t) {
            return "Super says: " + t;
        }
    }
    
    private class Sub extends Super<String> {
        @Override
        public String say(String t)  {
            return "Dummy says: " + t;
        }
    }

    Super mock;
    
    private void setMockWithDownCast(Super mock) {
        this.mock = mock;
    }
    
    private void say(String string) {
        mock.say(string);
    }
    
    @Test
    public void shouldHaveBridgeMethod() throws Exception {
        Super s = new Sub();
        
        assertEquals("Dummy says: Hello", s.say("Hello"));
        
        assertThat(Sub.class, hasBridgeMethod("say"));
        assertThat(s, hasBridgeMethod("say"));
    }
    
    @Test
    public void shouldVerifyCorrectlyWhenBridgeMethodCalled() throws Exception {
        //Super has following erasure: say(Object) which differs from Dummy.say(String)
        //mock has to detect it and do the super.say()
        //see MockFactory.java
        Sub s = mock(Sub.class);
        setMockWithDownCast(s);
        say("Hello");
        
        verify(s).say("Hello");
    }
}