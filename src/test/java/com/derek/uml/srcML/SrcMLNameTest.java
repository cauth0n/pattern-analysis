package com.derek.uml.srcML;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import static org.junit.Assert.*;

public class SrcMLNameTest extends SrcMLTest{
    private SrcMLName srcMLName1;
    private SrcMLName srcMLName2;
    private SrcMLName srcMLName3;
    private SrcMLName srcMLName4;


    public SrcMLNameTest(){
        xmlFileLocation = "src/test/resources/SrcMLNameExample.xml";
    }

    @Before
    public void build(){
        super.build();
        Element nameEle1 = (Element) doc.getElementsByTagName("name").item(0);
        Element nameEle2 = (Element) doc.getElementsByTagName("name").item(1);
        Element nameEle3 = (Element) doc.getElementsByTagName("name").item(4);
        Element nameEle4 = (Element) doc.getElementsByTagName("name").item(9);
        srcMLName1 = new SrcMLName(nameEle1);
        srcMLName2 = new SrcMLName(nameEle2);
        srcMLName3 = new SrcMLName(nameEle3);
        srcMLName4 = new SrcMLName(nameEle4);
    }


    @Test
    public void verifyName(){
        assertEquals(srcMLName1.getNames().size(), 1);
        assertEquals(srcMLName1.getNames().peek().get(0), "First");
        assertEquals(srcMLName1.getName(), "First");
    }
    @Test
    public void verifyName2(){
        assertEquals(srcMLName2.getNames().size(), 2);
        assertEquals(srcMLName2.getNames().peek().get(0), "Optional");
        assertEquals(srcMLName2.getNames().peek().get(1), "Result");
    }
    @Test
    public void verifyName3(){
        assertEquals(srcMLName3.getNames().size(), 3);
        assertEquals(srcMLName3.getNames().peek().get(0), "ExpectedCondition");
        assertEquals(srcMLName3.getNames().peek().get(1), "List");
        assertEquals(srcMLName3.getNames().peek().get(2), "WebElement");
    }
    @Test
    public void verifyName4(){
        assertEquals(srcMLName4.getNames().size(), 2);
        assertEquals(srcMLName4.getNames().peek().get(0), "element");
        assertEquals(srcMLName4.getNames().peek().get(1), "isEnabled");
    }

    @Test
    public void verifyOperators(){
        assertEquals(srcMLName1.getOperators().size(), 0);
        assertEquals(srcMLName2.getOperators().size(), 0);
        assertEquals(srcMLName3.getOperators().size(), 0);
        assertEquals(srcMLName4.getOperators().size(), 1);
        assertEquals(srcMLName4.getOperators().get(0), ".");
    }

}