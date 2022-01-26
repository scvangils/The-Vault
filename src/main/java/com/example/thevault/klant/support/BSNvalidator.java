/*@author Wim 20211202
methode voor valideren BSN
van https://gist.github.com/barend/1018771
 */

package com.example.thevault.klant.support;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class BSNvalidator {

public static long TESTBSN_VAN_RIVG = 999997683;

    //TODO JavaDoc
    public static boolean bsnValideren(long bsn){
        if (bsn <= 9999999 || bsn > 999999999){
            return false;
        }
        long som = -1 * bsn % 10;

        for (int vermenigvuldiger = 2; bsn > 0; vermenigvuldiger++) {
            long waarde = (bsn /= 10) % 10;
            som += vermenigvuldiger * waarde;
        }
        return som != 0 && som % 11 == 0;
    }
}
