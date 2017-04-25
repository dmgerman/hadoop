begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A util to convert values in one unit to another. Units refers to whether  * the value is expressed in pico, nano, etc.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|UnitsConversionUtil
specifier|public
class|class
name|UnitsConversionUtil
block|{
comment|/**    * Helper class for encapsulating conversion values.    */
DECL|class|Converter
specifier|public
specifier|static
class|class
name|Converter
block|{
DECL|field|numerator
specifier|private
name|long
name|numerator
decl_stmt|;
DECL|field|denominator
specifier|private
name|long
name|denominator
decl_stmt|;
DECL|method|Converter (long n, long d)
name|Converter
parameter_list|(
name|long
name|n
parameter_list|,
name|long
name|d
parameter_list|)
block|{
name|this
operator|.
name|numerator
operator|=
name|n
expr_stmt|;
name|this
operator|.
name|denominator
operator|=
name|d
expr_stmt|;
block|}
block|}
DECL|field|UNITS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|UNITS
init|=
block|{
literal|"p"
block|,
literal|"n"
block|,
literal|"u"
block|,
literal|"m"
block|,
literal|""
block|,
literal|"k"
block|,
literal|"M"
block|,
literal|"G"
block|,
literal|"T"
block|,
literal|"P"
block|,
literal|"Ki"
block|,
literal|"Mi"
block|,
literal|"Gi"
block|,
literal|"Ti"
block|,
literal|"Pi"
block|}
decl_stmt|;
DECL|field|SORTED_UNITS
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|SORTED_UNITS
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|UNITS
argument_list|)
decl_stmt|;
DECL|field|KNOWN_UNITS
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|KNOWN_UNITS
init|=
name|createKnownUnitsSet
argument_list|()
decl_stmt|;
DECL|field|PICO
specifier|private
specifier|static
specifier|final
name|Converter
name|PICO
init|=
operator|new
name|Converter
argument_list|(
literal|1L
argument_list|,
literal|1000L
operator|*
literal|1000L
operator|*
literal|1000L
operator|*
literal|1000L
argument_list|)
decl_stmt|;
DECL|field|NANO
specifier|private
specifier|static
specifier|final
name|Converter
name|NANO
init|=
operator|new
name|Converter
argument_list|(
literal|1L
argument_list|,
literal|1000L
operator|*
literal|1000L
operator|*
literal|1000L
argument_list|)
decl_stmt|;
DECL|field|MICRO
specifier|private
specifier|static
specifier|final
name|Converter
name|MICRO
init|=
operator|new
name|Converter
argument_list|(
literal|1L
argument_list|,
literal|1000L
operator|*
literal|1000L
argument_list|)
decl_stmt|;
DECL|field|MILLI
specifier|private
specifier|static
specifier|final
name|Converter
name|MILLI
init|=
operator|new
name|Converter
argument_list|(
literal|1L
argument_list|,
literal|1000L
argument_list|)
decl_stmt|;
DECL|field|BASE
specifier|private
specifier|static
specifier|final
name|Converter
name|BASE
init|=
operator|new
name|Converter
argument_list|(
literal|1L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|KILO
specifier|private
specifier|static
specifier|final
name|Converter
name|KILO
init|=
operator|new
name|Converter
argument_list|(
literal|1000L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|MEGA
specifier|private
specifier|static
specifier|final
name|Converter
name|MEGA
init|=
operator|new
name|Converter
argument_list|(
literal|1000L
operator|*
literal|1000L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|GIGA
specifier|private
specifier|static
specifier|final
name|Converter
name|GIGA
init|=
operator|new
name|Converter
argument_list|(
literal|1000L
operator|*
literal|1000L
operator|*
literal|1000L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|TERA
specifier|private
specifier|static
specifier|final
name|Converter
name|TERA
init|=
operator|new
name|Converter
argument_list|(
literal|1000L
operator|*
literal|1000L
operator|*
literal|1000L
operator|*
literal|1000L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|PETA
specifier|private
specifier|static
specifier|final
name|Converter
name|PETA
init|=
operator|new
name|Converter
argument_list|(
literal|1000L
operator|*
literal|1000L
operator|*
literal|1000L
operator|*
literal|1000L
operator|*
literal|1000L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|KILO_BINARY
specifier|private
specifier|static
specifier|final
name|Converter
name|KILO_BINARY
init|=
operator|new
name|Converter
argument_list|(
literal|1024L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|MEGA_BINARY
specifier|private
specifier|static
specifier|final
name|Converter
name|MEGA_BINARY
init|=
operator|new
name|Converter
argument_list|(
literal|1024L
operator|*
literal|1024L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|GIGA_BINARY
specifier|private
specifier|static
specifier|final
name|Converter
name|GIGA_BINARY
init|=
operator|new
name|Converter
argument_list|(
literal|1024L
operator|*
literal|1024L
operator|*
literal|1024L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|TERA_BINARY
specifier|private
specifier|static
specifier|final
name|Converter
name|TERA_BINARY
init|=
operator|new
name|Converter
argument_list|(
literal|1024L
operator|*
literal|1024L
operator|*
literal|1024L
operator|*
literal|1024L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|field|PETA_BINARY
specifier|private
specifier|static
specifier|final
name|Converter
name|PETA_BINARY
init|=
operator|new
name|Converter
argument_list|(
literal|1024L
operator|*
literal|1024L
operator|*
literal|1024L
operator|*
literal|1024L
operator|*
literal|1024L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
DECL|method|createKnownUnitsSet ()
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|createKnownUnitsSet
parameter_list|()
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ret
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|ret
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|UNITS
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|getConverter (String unit)
specifier|private
specifier|static
name|Converter
name|getConverter
parameter_list|(
name|String
name|unit
parameter_list|)
block|{
switch|switch
condition|(
name|unit
condition|)
block|{
case|case
literal|"p"
case|:
return|return
name|PICO
return|;
case|case
literal|"n"
case|:
return|return
name|NANO
return|;
case|case
literal|"u"
case|:
return|return
name|MICRO
return|;
case|case
literal|"m"
case|:
return|return
name|MILLI
return|;
case|case
literal|""
case|:
return|return
name|BASE
return|;
case|case
literal|"k"
case|:
return|return
name|KILO
return|;
case|case
literal|"M"
case|:
return|return
name|MEGA
return|;
case|case
literal|"G"
case|:
return|return
name|GIGA
return|;
case|case
literal|"T"
case|:
return|return
name|TERA
return|;
case|case
literal|"P"
case|:
return|return
name|PETA
return|;
case|case
literal|"Ki"
case|:
return|return
name|KILO_BINARY
return|;
case|case
literal|"Mi"
case|:
return|return
name|MEGA_BINARY
return|;
case|case
literal|"Gi"
case|:
return|return
name|GIGA_BINARY
return|;
case|case
literal|"Ti"
case|:
return|return
name|TERA_BINARY
return|;
case|case
literal|"Pi"
case|:
return|return
name|PETA_BINARY
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown unit '"
operator|+
name|unit
operator|+
literal|"'. Known units are "
operator|+
name|KNOWN_UNITS
argument_list|)
throw|;
block|}
block|}
comment|/**    * Converts a value from one unit to another. Supported units can be obtained    * by inspecting the KNOWN_UNITS set.    *    * @param fromUnit  the unit of the from value    * @param toUnit    the target unit    * @param fromValue the value you wish to convert    * @return the value in toUnit    */
DECL|method|convert (String fromUnit, String toUnit, long fromValue)
specifier|public
specifier|static
name|long
name|convert
parameter_list|(
name|String
name|fromUnit
parameter_list|,
name|String
name|toUnit
parameter_list|,
name|long
name|fromValue
parameter_list|)
block|{
if|if
condition|(
name|toUnit
operator|==
literal|null
operator|||
name|fromUnit
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"One or more arguments are null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|fromUnit
operator|.
name|equals
argument_list|(
name|toUnit
argument_list|)
condition|)
block|{
return|return
name|fromValue
return|;
block|}
name|Converter
name|fc
init|=
name|getConverter
argument_list|(
name|fromUnit
argument_list|)
decl_stmt|;
name|Converter
name|tc
init|=
name|getConverter
argument_list|(
name|toUnit
argument_list|)
decl_stmt|;
name|long
name|numerator
init|=
name|fc
operator|.
name|numerator
operator|*
name|tc
operator|.
name|denominator
decl_stmt|;
name|long
name|denominator
init|=
name|fc
operator|.
name|denominator
operator|*
name|tc
operator|.
name|numerator
decl_stmt|;
name|long
name|numeratorMultiplierLimit
init|=
name|Long
operator|.
name|MAX_VALUE
operator|/
name|numerator
decl_stmt|;
if|if
condition|(
name|numerator
operator|<
name|denominator
condition|)
block|{
if|if
condition|(
name|numeratorMultiplierLimit
operator|<
name|fromValue
condition|)
block|{
name|String
name|overflowMsg
init|=
literal|"Converting "
operator|+
name|fromValue
operator|+
literal|" from '"
operator|+
name|fromUnit
operator|+
literal|"' to '"
operator|+
name|toUnit
operator|+
literal|"' will result in an overflow of Long"
decl_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|overflowMsg
argument_list|)
throw|;
block|}
return|return
operator|(
name|fromValue
operator|*
name|numerator
operator|)
operator|/
name|denominator
return|;
block|}
if|if
condition|(
name|numeratorMultiplierLimit
operator|>
name|fromValue
condition|)
block|{
return|return
operator|(
name|numerator
operator|*
name|fromValue
operator|)
operator|/
name|denominator
return|;
block|}
name|long
name|tmp
init|=
name|numerator
operator|/
name|denominator
decl_stmt|;
if|if
condition|(
operator|(
name|Long
operator|.
name|MAX_VALUE
operator|/
name|tmp
operator|)
operator|<
name|fromValue
condition|)
block|{
name|String
name|overflowMsg
init|=
literal|"Converting "
operator|+
name|fromValue
operator|+
literal|" from '"
operator|+
name|fromUnit
operator|+
literal|"' to '"
operator|+
name|toUnit
operator|+
literal|"' will result in an overflow of Long"
decl_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|overflowMsg
argument_list|)
throw|;
block|}
return|return
name|fromValue
operator|*
name|tmp
return|;
block|}
comment|/**    * Compare a value in a given unit with a value in another unit. The return    * value is equivalent to the value returned by compareTo.    *    * @param unitA  first unit    * @param valueA first value    * @param unitB  second unit    * @param valueB second value    * @return +1, 0 or -1 depending on whether the relationship is greater than,    * equal to or lesser than    */
DECL|method|compare (String unitA, long valueA, String unitB, long valueB)
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|String
name|unitA
parameter_list|,
name|long
name|valueA
parameter_list|,
name|String
name|unitB
parameter_list|,
name|long
name|valueB
parameter_list|)
block|{
if|if
condition|(
name|unitA
operator|==
literal|null
operator|||
name|unitB
operator|==
literal|null
operator|||
operator|!
name|KNOWN_UNITS
operator|.
name|contains
argument_list|(
name|unitA
argument_list|)
operator|||
operator|!
name|KNOWN_UNITS
operator|.
name|contains
argument_list|(
name|unitB
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Units cannot be null"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|KNOWN_UNITS
operator|.
name|contains
argument_list|(
name|unitA
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown unit '"
operator|+
name|unitA
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|KNOWN_UNITS
operator|.
name|contains
argument_list|(
name|unitB
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown unit '"
operator|+
name|unitB
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|Converter
name|unitAC
init|=
name|getConverter
argument_list|(
name|unitA
argument_list|)
decl_stmt|;
name|Converter
name|unitBC
init|=
name|getConverter
argument_list|(
name|unitB
argument_list|)
decl_stmt|;
if|if
condition|(
name|unitA
operator|.
name|equals
argument_list|(
name|unitB
argument_list|)
condition|)
block|{
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|valueA
argument_list|)
operator|.
name|compareTo
argument_list|(
name|valueB
argument_list|)
return|;
block|}
name|int
name|unitAPos
init|=
name|SORTED_UNITS
operator|.
name|indexOf
argument_list|(
name|unitA
argument_list|)
decl_stmt|;
name|int
name|unitBPos
init|=
name|SORTED_UNITS
operator|.
name|indexOf
argument_list|(
name|unitB
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|tmpA
init|=
name|valueA
decl_stmt|;
name|long
name|tmpB
init|=
name|valueB
decl_stmt|;
if|if
condition|(
name|unitAPos
operator|<
name|unitBPos
condition|)
block|{
name|tmpB
operator|=
name|convert
argument_list|(
name|unitB
argument_list|,
name|unitA
argument_list|,
name|valueB
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tmpA
operator|=
name|convert
argument_list|(
name|unitA
argument_list|,
name|unitB
argument_list|,
name|valueA
argument_list|)
expr_stmt|;
block|}
return|return
name|Long
operator|.
name|valueOf
argument_list|(
name|tmpA
argument_list|)
operator|.
name|compareTo
argument_list|(
name|tmpB
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ie
parameter_list|)
block|{
name|BigInteger
name|tmpA
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|valueA
argument_list|)
decl_stmt|;
name|BigInteger
name|tmpB
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|valueB
argument_list|)
decl_stmt|;
if|if
condition|(
name|unitAPos
operator|<
name|unitBPos
condition|)
block|{
name|tmpB
operator|=
name|tmpB
operator|.
name|multiply
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|unitBC
operator|.
name|numerator
argument_list|)
argument_list|)
expr_stmt|;
name|tmpB
operator|=
name|tmpB
operator|.
name|multiply
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|unitAC
operator|.
name|denominator
argument_list|)
argument_list|)
expr_stmt|;
name|tmpB
operator|=
name|tmpB
operator|.
name|divide
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|unitBC
operator|.
name|denominator
argument_list|)
argument_list|)
expr_stmt|;
name|tmpB
operator|=
name|tmpB
operator|.
name|divide
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|unitAC
operator|.
name|numerator
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tmpA
operator|=
name|tmpA
operator|.
name|multiply
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|unitAC
operator|.
name|numerator
argument_list|)
argument_list|)
expr_stmt|;
name|tmpA
operator|=
name|tmpA
operator|.
name|multiply
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|unitBC
operator|.
name|denominator
argument_list|)
argument_list|)
expr_stmt|;
name|tmpA
operator|=
name|tmpA
operator|.
name|divide
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|unitAC
operator|.
name|denominator
argument_list|)
argument_list|)
expr_stmt|;
name|tmpA
operator|=
name|tmpA
operator|.
name|divide
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|unitBC
operator|.
name|numerator
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|tmpA
operator|.
name|compareTo
argument_list|(
name|tmpB
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

