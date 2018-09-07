package ru.rewindforce.concerts;

import android.content.Context;
import android.test.mock.MockContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.rewindforce.concerts.Views.FloatingMultiActionButton;
import static org.junit.Assert.assertEquals;
public class FloatingMultiActionButtonTest {

    private FloatingMultiActionButton fmab;

    @Before
    public void setUp(){
        fmab = new FloatingMultiActionButton(new MockContext());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInsertSecondItemWithIdThatIsAlreadyInList(){
        fmab    .addItem(new FloatingMultiActionButton.Item(1))
                .addItem(new FloatingMultiActionButton.Item(0).setName("Name"))
                .addItem(new FloatingMultiActionButton.Item(0).setName("3"));
    }

    @After
    public void tearDown(){
        fmab = null;
    }
}
