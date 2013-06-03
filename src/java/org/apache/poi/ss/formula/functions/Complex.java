package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.*;

/**
 * Implementation for Excel COMPLEX () function.<p/>
 * <p/>
 * <b>Syntax</b>:<br/> <b>COMPLEX   </b>(<b>real_num</b>,<b>i_num</b>,<b>suffix </b> )<br/>
 * <p/>
 * Converts real and imaginary coefficients into a complex number of the form x + yi or x + yj.
 * <p/>
 * <p/>
 * All complex number functions accept "i" and "j" for suffix, but neither "I" nor "J".
 * Using uppercase results in the #VALUE! error value. All functions that accept two
 * or more complex numbers require that all suffixes match.
 * <p/>
 * <b>real_num</b> The real coefficient of the complex number.
 * If this argument is nonnumeric, this function returns the #VALUE! error value.
 * <p/>
 * <p/>
 * <b>i_num</b> The imaginary coefficient of the complex number.
 * If this argument is nonnumeric, this function returns the #VALUE! error value.
 * <p/>
 * <p/>
 * <b>suffix</b> The suffix for the imaginary component of the complex number.
 * <ul>
 * <li>If omitted, suffix is assumed to be "i".</li>
 * <li>If suffix is neither "i" nor "j", COMPLEX returns the #VALUE! error value.</li>
 * </ul>
 *
 * @author cedric dot walter @ gmail dot com
 */
public class Complex extends Var2or3ArgFunction implements FreeRefFunction {

    public static final FreeRefFunction instance = new Complex();

    public static final String DEFAULT_SUFFIX = "i";
    public static final String SUPPORTED_SUFFIX = "j";

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval real_num, ValueEval i_num) {
        return this.evaluate(srcRowIndex, srcColumnIndex, real_num, i_num, new StringEval(DEFAULT_SUFFIX));
    }

    public ValueEval evaluate(int srcRowIndex, int srcColumnIndex, ValueEval real_num, ValueEval i_num, ValueEval suffix) {
        ValueEval veText1;
        try {
            veText1 = OperandResolver.getSingleValue(real_num, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        double realNum = 0;
        try {
            realNum = OperandResolver.coerceValueToDouble(veText1);
        } catch (EvaluationException e) {
            return ErrorEval.VALUE_INVALID;
        }

        ValueEval veINum;
        try {
            veINum = OperandResolver.getSingleValue(i_num, srcRowIndex, srcColumnIndex);
        } catch (EvaluationException e) {
            return e.getErrorEval();
        }
        double realINum = 0;
        try {
            realINum = OperandResolver.coerceValueToDouble(veINum);
        } catch (EvaluationException e) {
            return ErrorEval.VALUE_INVALID;
        }

        String suffixValue = OperandResolver.coerceValueToString(suffix);
        if (suffixValue.isEmpty()) {
            suffixValue = DEFAULT_SUFFIX;
        }
        if (suffixValue.equals(DEFAULT_SUFFIX.toUpperCase()) || suffixValue.equals(SUPPORTED_SUFFIX.toUpperCase())) {
            return ErrorEval.VALUE_INVALID;
        }
        if (!(suffixValue.equals(DEFAULT_SUFFIX) || suffixValue.equals(SUPPORTED_SUFFIX))) {
            return ErrorEval.VALUE_INVALID;
        }

        StringBuffer strb = new StringBuffer("");
        if (realNum != 0) {
            if (isDoubleAnInt(realNum)) {
                strb.append(new Double(realNum).intValue());
            } else {
                strb.append(realNum);
            }
        }
        if (realINum != 0) {
            if (strb.length() != 0) {
                if (realINum > 0) {
                    strb.append("+");
                }
            }

            if (realINum != 1 && realINum != -1) {
                if (isDoubleAnInt(realINum)) {
                    strb.append(new Double(realINum).intValue());
                } else {
                    strb.append(realINum);
                }
            }

            strb.append(suffixValue);
        }

        return new StringEval(strb.toString());
    }

    /**
     * @param number
     * @return
     */
    private boolean isDoubleAnInt(double number) {
        return (number == Math.floor(number)) && !Double.isInfinite(number);
    }

    public ValueEval evaluate(ValueEval[] args, OperationEvaluationContext ec) {
        if (args.length == 2) {
            return evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        if (args.length == 3) {
            return evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1], args[2]);
        }

        return ErrorEval.VALUE_INVALID;
    }
}
