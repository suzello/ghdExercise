package com.ghd_test.app;

import com.ghd_test.business.TaxCalculation;

/**
 * Entry point
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	TaxCalculation taxCalculation = new TaxCalculation();
    	taxCalculation.calcualateTaxandPrint();
    }
}
