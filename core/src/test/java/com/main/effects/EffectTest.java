package com.main.effects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class EffectTest {

    // Classe concrète pour tester la classe abstraite Effect
    private class TestEffect extends Effect {
        public TestEffect(String name, Type type) {
            super(name, type);
        }
    }

    @Test
    public void testConstructor() {
        Effect effect = new TestEffect("testEffect", Effect.Type.BUFF);
        assertNotNull(effect);
        assertEquals("testEffect", effect.getName());
        assertEquals(Effect.Type.BUFF, effect.getType());
    }

    @Test
    public void testGetName() {
        Effect effect = new TestEffect("myEffect", Effect.Type.DEBUFF);
        assertEquals("myEffect", effect.getName());
    }

    @Test
    public void testGetType() {
        Effect effect = new TestEffect("effect", Effect.Type.AOE);
        assertEquals(Effect.Type.AOE, effect.getType());
    }

    @Test
    public void testAllTypes() {
        Effect buff = new TestEffect("buff", Effect.Type.BUFF);
        Effect debuff = new TestEffect("debuff", Effect.Type.DEBUFF);
        Effect aoe = new TestEffect("aoe", Effect.Type.AOE);
        Effect dot = new TestEffect("dot", Effect.Type.DOT);
        Effect damage = new TestEffect("damage", Effect.Type.DAMAGE);
        
        assertEquals(Effect.Type.BUFF, buff.getType());
        assertEquals(Effect.Type.DEBUFF, debuff.getType());
        assertEquals(Effect.Type.AOE, aoe.getType());
        assertEquals(Effect.Type.DOT, dot.getType());
        assertEquals(Effect.Type.DAMAGE, damage.getType());
    }
}