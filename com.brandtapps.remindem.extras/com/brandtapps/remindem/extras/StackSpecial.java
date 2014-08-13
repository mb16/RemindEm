package com.brandtapps.remindem.extras;

import java.util.List;
import java.util.ArrayList;


public class StackSpecial 
{

    private List<Special> stack;

    public StackSpecial() 
    {
        stack = new ArrayList<Special>();
    }

    public void push(Special i) 
    {
       stack.add(0,i);
     }

     public Special pop() 
     { 
        if(!stack.isEmpty()){
           Special i= stack.get(0);
           stack.remove(0);
           return i;
        } else{
           return null;// Or any invalid value
        }
     }

     public Special peek()
     {
        if(!stack.isEmpty()){
           return stack.get(0);
        } else{
           return null;// Or any invalid value
        }
     }

     public boolean isEmpty() 
     {
    	 return stack.isEmpty();
     }
 }
