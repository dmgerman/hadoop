begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples.pi.math
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|pi
operator|.
name|math
package|;
end_package

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
name|Random
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
name|examples
operator|.
name|pi
operator|.
name|Util
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestModular
specifier|public
class|class
name|TestModular
block|{
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|TWO
specifier|private
specifier|static
specifier|final
name|BigInteger
name|TWO
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
decl_stmt|;
DECL|field|DIV_VALID_BIT
specifier|static
specifier|final
name|int
name|DIV_VALID_BIT
init|=
literal|32
decl_stmt|;
DECL|field|DIV_LIMIT
specifier|static
specifier|final
name|long
name|DIV_LIMIT
init|=
literal|1L
operator|<<
name|DIV_VALID_BIT
decl_stmt|;
comment|// return r/n for n> r> 0
DECL|method|div (long sum, long r, long n)
specifier|static
name|long
name|div
parameter_list|(
name|long
name|sum
parameter_list|,
name|long
name|r
parameter_list|,
name|long
name|n
parameter_list|)
block|{
name|long
name|q
init|=
literal|0
decl_stmt|;
name|int
name|i
init|=
name|DIV_VALID_BIT
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|r
operator|<<=
literal|1
init|;
name|r
operator|<
name|n
condition|;
name|r
operator|<<=
literal|1
control|)
name|i
operator|--
expr_stmt|;
comment|//System.out.printf("  r=%d, n=%d, q=%d\n", r, n, q);
for|for
control|(
init|;
name|i
operator|>=
literal|0
condition|;
control|)
block|{
name|r
operator|-=
name|n
expr_stmt|;
name|q
operator||=
operator|(
literal|1L
operator|<<
name|i
operator|)
expr_stmt|;
if|if
condition|(
name|r
operator|<=
literal|0
condition|)
break|break;
for|for
control|(
init|;
name|r
operator|<
name|n
condition|;
name|r
operator|<<=
literal|1
control|)
name|i
operator|--
expr_stmt|;
comment|//System.out.printf("  r=%d, n=%d, q=%d\n", r, n, q);
block|}
name|sum
operator|+=
name|q
expr_stmt|;
return|return
name|sum
operator|<
name|DIV_LIMIT
condition|?
name|sum
else|:
name|sum
operator|-
name|DIV_LIMIT
return|;
block|}
annotation|@
name|Test
DECL|method|testDiv ()
specifier|public
name|void
name|testDiv
parameter_list|()
block|{
for|for
control|(
name|long
name|n
init|=
literal|2
init|;
name|n
operator|<
literal|100
condition|;
name|n
operator|++
control|)
for|for
control|(
name|long
name|r
init|=
literal|1
init|;
name|r
operator|<
name|n
condition|;
name|r
operator|++
control|)
block|{
specifier|final
name|long
name|a
init|=
name|div
argument_list|(
literal|0
argument_list|,
name|r
argument_list|,
name|n
argument_list|)
decl_stmt|;
specifier|final
name|long
name|b
init|=
call|(
name|long
call|)
argument_list|(
operator|(
name|r
operator|*
literal|1.0
operator|/
name|n
operator|)
operator|*
operator|(
literal|1L
operator|<<
name|DIV_VALID_BIT
operator|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s
init|=
name|String
operator|.
name|format
argument_list|(
literal|"r=%d, n=%d, a=%X, b=%X"
argument_list|,
name|r
argument_list|,
name|n
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|s
argument_list|,
name|b
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|generateRN (int nsize, int rsize)
specifier|static
name|long
index|[]
index|[]
index|[]
name|generateRN
parameter_list|(
name|int
name|nsize
parameter_list|,
name|int
name|rsize
parameter_list|)
block|{
specifier|final
name|long
index|[]
index|[]
index|[]
name|rn
init|=
operator|new
name|long
index|[
name|nsize
index|]
index|[]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rn
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|rn
index|[
name|i
index|]
operator|=
operator|new
name|long
index|[
name|rsize
operator|+
literal|1
index|]
index|[]
expr_stmt|;
name|long
name|n
init|=
name|RANDOM
operator|.
name|nextLong
argument_list|()
operator|&
literal|0xFFFFFFFFFFFFFFFL
decl_stmt|;
if|if
condition|(
name|n
operator|<=
literal|1
condition|)
name|n
operator|=
literal|0xFFFFFFFFFFFFFFFL
operator|-
name|n
expr_stmt|;
name|rn
index|[
name|i
index|]
index|[
literal|0
index|]
operator|=
operator|new
name|long
index|[]
block|{
name|n
block|}
expr_stmt|;
specifier|final
name|BigInteger
name|N
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|n
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|rn
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|long
name|r
init|=
name|RANDOM
operator|.
name|nextLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|<
literal|0
condition|)
name|r
operator|=
operator|-
name|r
expr_stmt|;
if|if
condition|(
name|r
operator|>=
name|n
condition|)
name|r
operator|%=
name|n
expr_stmt|;
specifier|final
name|BigInteger
name|R
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|rn
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
operator|new
name|long
index|[]
block|{
name|r
block|,
name|R
operator|.
name|multiply
argument_list|(
name|R
argument_list|)
operator|.
name|mod
argument_list|(
name|N
argument_list|)
operator|.
name|longValue
argument_list|()
block|}
expr_stmt|;
block|}
block|}
return|return
name|rn
return|;
block|}
DECL|method|square_slow (long z, final long n)
specifier|static
name|long
name|square_slow
parameter_list|(
name|long
name|z
parameter_list|,
specifier|final
name|long
name|n
parameter_list|)
block|{
name|long
name|r
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
name|s
init|=
name|z
init|;
name|z
operator|>
literal|0
condition|;
name|z
operator|>>=
literal|1
control|)
block|{
if|if
condition|(
operator|(
operator|(
operator|(
name|int
operator|)
name|z
operator|)
operator|&
literal|1
operator|)
operator|==
literal|1
condition|)
block|{
name|r
operator|+=
name|s
expr_stmt|;
if|if
condition|(
name|r
operator|>=
name|n
condition|)
name|r
operator|-=
name|n
expr_stmt|;
block|}
name|s
operator|<<=
literal|1
expr_stmt|;
if|if
condition|(
name|s
operator|>=
name|n
condition|)
name|s
operator|-=
name|n
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
comment|//0<= r< n< max/2
DECL|method|square (long r, final long n, long r2p64)
specifier|static
name|long
name|square
parameter_list|(
name|long
name|r
parameter_list|,
specifier|final
name|long
name|n
parameter_list|,
name|long
name|r2p64
parameter_list|)
block|{
if|if
condition|(
name|r
operator|<=
name|Modular
operator|.
name|MAX_SQRT_LONG
condition|)
block|{
name|r
operator|*=
name|r
expr_stmt|;
if|if
condition|(
name|r
operator|>=
name|n
condition|)
name|r
operator|%=
name|n
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|HALF
init|=
operator|(
literal|63
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|n
argument_list|)
operator|)
operator|>>
literal|1
decl_stmt|;
specifier|final
name|int
name|FULL
init|=
name|HALF
operator|<<
literal|1
decl_stmt|;
specifier|final
name|long
name|ONES
init|=
operator|(
literal|1
operator|<<
name|HALF
operator|)
operator|-
literal|1
decl_stmt|;
specifier|final
name|long
name|high
init|=
name|r
operator|>>>
name|HALF
decl_stmt|;
specifier|final
name|long
name|low
init|=
name|r
operator|&=
name|ONES
decl_stmt|;
name|r
operator|*=
name|r
expr_stmt|;
if|if
condition|(
name|r
operator|>=
name|n
condition|)
name|r
operator|%=
name|n
expr_stmt|;
if|if
condition|(
name|high
operator|!=
literal|0
condition|)
block|{
name|long
name|s
init|=
name|high
operator|*
name|high
decl_stmt|;
if|if
condition|(
name|s
operator|>=
name|n
condition|)
name|s
operator|%=
name|n
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|FULL
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|(
name|s
operator|<<=
literal|1
operator|)
operator|>=
name|n
condition|)
name|s
operator|-=
name|n
expr_stmt|;
if|if
condition|(
name|low
operator|==
literal|0
condition|)
name|r
operator|=
name|s
expr_stmt|;
else|else
block|{
name|long
name|t
init|=
name|high
operator|*
name|low
decl_stmt|;
if|if
condition|(
name|t
operator|>=
name|n
condition|)
name|t
operator|%=
name|n
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
operator|-
literal|1
init|;
name|i
operator|<
name|HALF
condition|;
name|i
operator|++
control|)
if|if
condition|(
operator|(
name|t
operator|<<=
literal|1
operator|)
operator|>=
name|n
condition|)
name|t
operator|-=
name|n
expr_stmt|;
name|r
operator|+=
name|s
expr_stmt|;
if|if
condition|(
name|r
operator|>=
name|n
condition|)
name|r
operator|-=
name|n
expr_stmt|;
name|r
operator|+=
name|t
expr_stmt|;
if|if
condition|(
name|r
operator|>=
name|n
condition|)
name|r
operator|-=
name|n
expr_stmt|;
block|}
block|}
block|}
return|return
name|r
return|;
block|}
DECL|method|squareBenchmarks ()
specifier|static
name|void
name|squareBenchmarks
parameter_list|()
block|{
specifier|final
name|Timer
name|t
init|=
operator|new
name|Timer
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|t
operator|.
name|tick
argument_list|(
literal|"squareBenchmarks(), MAX_SQRT="
operator|+
name|Modular
operator|.
name|MAX_SQRT_LONG
argument_list|)
expr_stmt|;
specifier|final
name|long
index|[]
index|[]
index|[]
name|rn
init|=
name|generateRN
argument_list|(
literal|1000
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|t
operator|.
name|tick
argument_list|(
literal|"generateRN"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rn
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|n
init|=
name|rn
index|[
name|i
index|]
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|rn
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|long
name|r
init|=
name|rn
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|long
name|answer
init|=
name|rn
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|long
name|s
init|=
name|square_slow
argument_list|(
name|r
argument_list|,
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
name|answer
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"r="
operator|+
name|r
operator|+
literal|", n="
operator|+
name|n
operator|+
literal|", answer="
operator|+
name|answer
operator|+
literal|" but s="
operator|+
name|s
argument_list|,
name|answer
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|t
operator|.
name|tick
argument_list|(
literal|"square_slow"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rn
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|n
init|=
name|rn
index|[
name|i
index|]
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
name|long
name|r2p64
init|=
operator|(
literal|0x4000000000000000L
operator|%
name|n
operator|)
operator|<<
literal|1
decl_stmt|;
if|if
condition|(
name|r2p64
operator|>=
name|n
condition|)
name|r2p64
operator|-=
name|n
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|rn
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|long
name|r
init|=
name|rn
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|long
name|answer
init|=
name|rn
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|long
name|s
init|=
name|square
argument_list|(
name|r
argument_list|,
name|n
argument_list|,
name|r2p64
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
name|answer
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"r="
operator|+
name|r
operator|+
literal|", n="
operator|+
name|n
operator|+
literal|", answer="
operator|+
name|answer
operator|+
literal|" but s="
operator|+
name|s
argument_list|,
name|answer
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|t
operator|.
name|tick
argument_list|(
literal|"square"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rn
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|n
init|=
name|rn
index|[
name|i
index|]
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|BigInteger
name|N
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|n
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|rn
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|long
name|r
init|=
name|rn
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|long
name|answer
init|=
name|rn
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|BigInteger
name|R
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|r
argument_list|)
decl_stmt|;
specifier|final
name|long
name|s
init|=
name|R
operator|.
name|multiply
argument_list|(
name|R
argument_list|)
operator|.
name|mod
argument_list|(
name|N
argument_list|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|!=
name|answer
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"r="
operator|+
name|r
operator|+
literal|", n="
operator|+
name|n
operator|+
literal|", answer="
operator|+
name|answer
operator|+
literal|" but s="
operator|+
name|s
argument_list|,
name|answer
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|t
operator|.
name|tick
argument_list|(
literal|"R.multiply(R).mod(N)"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rn
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|n
init|=
name|rn
index|[
name|i
index|]
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|BigInteger
name|N
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|n
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|rn
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|long
name|r
init|=
name|rn
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|long
name|answer
init|=
name|rn
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|BigInteger
name|R
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|r
argument_list|)
decl_stmt|;
specifier|final
name|long
name|s
init|=
name|R
operator|.
name|modPow
argument_list|(
name|TWO
argument_list|,
name|N
argument_list|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|!=
name|answer
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"r="
operator|+
name|r
operator|+
literal|", n="
operator|+
name|n
operator|+
literal|", answer="
operator|+
name|answer
operator|+
literal|" but s="
operator|+
name|s
argument_list|,
name|answer
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|t
operator|.
name|tick
argument_list|(
literal|"R.modPow(TWO, N)"
argument_list|)
expr_stmt|;
block|}
DECL|method|generateEN (int nsize, int esize)
specifier|static
name|long
index|[]
index|[]
index|[]
name|generateEN
parameter_list|(
name|int
name|nsize
parameter_list|,
name|int
name|esize
parameter_list|)
block|{
specifier|final
name|long
index|[]
index|[]
index|[]
name|en
init|=
operator|new
name|long
index|[
name|nsize
index|]
index|[]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|en
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|en
index|[
name|i
index|]
operator|=
operator|new
name|long
index|[
name|esize
operator|+
literal|1
index|]
index|[]
expr_stmt|;
name|long
name|n
init|=
operator|(
name|RANDOM
operator|.
name|nextLong
argument_list|()
operator|&
literal|0xFFFFFFFFFFFFFFFL
operator|)
operator||
literal|1L
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|1
condition|)
name|n
operator|=
literal|3
expr_stmt|;
name|en
index|[
name|i
index|]
index|[
literal|0
index|]
operator|=
operator|new
name|long
index|[]
block|{
name|n
block|}
expr_stmt|;
specifier|final
name|BigInteger
name|N
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|n
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|en
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|long
name|e
init|=
name|RANDOM
operator|.
name|nextLong
argument_list|()
decl_stmt|;
if|if
condition|(
name|e
operator|<
literal|0
condition|)
name|e
operator|=
operator|-
name|e
expr_stmt|;
specifier|final
name|BigInteger
name|E
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|en
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
operator|new
name|long
index|[]
block|{
name|e
block|,
name|TWO
operator|.
name|modPow
argument_list|(
name|E
argument_list|,
name|N
argument_list|)
operator|.
name|longValue
argument_list|()
block|}
expr_stmt|;
block|}
block|}
return|return
name|en
return|;
block|}
comment|/** Compute $2^e \mod n$ for e> 0, n> 2 */
DECL|method|modBigInteger (final long e, final long n)
specifier|static
name|long
name|modBigInteger
parameter_list|(
specifier|final
name|long
name|e
parameter_list|,
specifier|final
name|long
name|n
parameter_list|)
block|{
name|long
name|mask
init|=
operator|(
name|e
operator|&
literal|0xFFFFFFFF00000000L
operator|)
operator|==
literal|0
condition|?
literal|0x00000000FFFFFFFFL
else|:
literal|0xFFFFFFFF00000000L
decl_stmt|;
name|mask
operator|&=
operator|(
name|e
operator|&
literal|0xFFFF0000FFFF0000L
operator|&
name|mask
operator|)
operator|==
literal|0
condition|?
literal|0x0000FFFF0000FFFFL
else|:
literal|0xFFFF0000FFFF0000L
expr_stmt|;
name|mask
operator|&=
operator|(
name|e
operator|&
literal|0xFF00FF00FF00FF00L
operator|&
name|mask
operator|)
operator|==
literal|0
condition|?
literal|0x00FF00FF00FF00FFL
else|:
literal|0xFF00FF00FF00FF00L
expr_stmt|;
name|mask
operator|&=
operator|(
name|e
operator|&
literal|0xF0F0F0F0F0F0F0F0L
operator|&
name|mask
operator|)
operator|==
literal|0
condition|?
literal|0x0F0F0F0F0F0F0F0FL
else|:
literal|0xF0F0F0F0F0F0F0F0L
expr_stmt|;
name|mask
operator|&=
operator|(
name|e
operator|&
literal|0xCCCCCCCCCCCCCCCCL
operator|&
name|mask
operator|)
operator|==
literal|0
condition|?
literal|0x3333333333333333L
else|:
literal|0xCCCCCCCCCCCCCCCCL
expr_stmt|;
name|mask
operator|&=
operator|(
name|e
operator|&
literal|0xAAAAAAAAAAAAAAAAL
operator|&
name|mask
operator|)
operator|==
literal|0
condition|?
literal|0x5555555555555555L
else|:
literal|0xAAAAAAAAAAAAAAAAL
expr_stmt|;
specifier|final
name|BigInteger
name|N
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|long
name|r
init|=
literal|2
decl_stmt|;
for|for
control|(
name|mask
operator|>>=
literal|1
init|;
name|mask
operator|>
literal|0
condition|;
name|mask
operator|>>=
literal|1
control|)
block|{
if|if
condition|(
name|r
operator|<=
name|Modular
operator|.
name|MAX_SQRT_LONG
condition|)
block|{
name|r
operator|*=
name|r
expr_stmt|;
if|if
condition|(
name|r
operator|>=
name|n
condition|)
name|r
operator|%=
name|n
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|BigInteger
name|R
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|r
operator|=
name|R
operator|.
name|multiply
argument_list|(
name|R
argument_list|)
operator|.
name|mod
argument_list|(
name|N
argument_list|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|e
operator|&
name|mask
operator|)
operator|!=
literal|0
condition|)
block|{
name|r
operator|<<=
literal|1
expr_stmt|;
if|if
condition|(
name|r
operator|>=
name|n
condition|)
name|r
operator|-=
name|n
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
DECL|class|Montgomery2
specifier|static
class|class
name|Montgomery2
extends|extends
name|Montgomery
block|{
comment|/** Compute 2^y mod N for N odd. */
DECL|method|mod2 (final long y)
name|long
name|mod2
parameter_list|(
specifier|final
name|long
name|y
parameter_list|)
block|{
name|long
name|r0
init|=
name|R
operator|-
name|N
decl_stmt|;
name|long
name|r1
init|=
name|r0
operator|<<
literal|1
decl_stmt|;
if|if
condition|(
name|r1
operator|>=
name|N
condition|)
name|r1
operator|-=
name|N
expr_stmt|;
for|for
control|(
name|long
name|mask
init|=
name|Long
operator|.
name|highestOneBit
argument_list|(
name|y
argument_list|)
init|;
name|mask
operator|>
literal|0
condition|;
name|mask
operator|>>>=
literal|1
control|)
block|{
if|if
condition|(
operator|(
name|mask
operator|&
name|y
operator|)
operator|==
literal|0
condition|)
block|{
name|r1
operator|=
name|product
operator|.
name|m
argument_list|(
name|r0
argument_list|,
name|r1
argument_list|)
expr_stmt|;
name|r0
operator|=
name|product
operator|.
name|m
argument_list|(
name|r0
argument_list|,
name|r0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|r0
operator|=
name|product
operator|.
name|m
argument_list|(
name|r0
argument_list|,
name|r1
argument_list|)
expr_stmt|;
name|r1
operator|=
name|product
operator|.
name|m
argument_list|(
name|r1
argument_list|,
name|r1
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|product
operator|.
name|m
argument_list|(
name|r0
argument_list|,
literal|1
argument_list|)
return|;
block|}
block|}
DECL|method|modBenchmarks ()
specifier|static
name|void
name|modBenchmarks
parameter_list|()
block|{
specifier|final
name|Timer
name|t
init|=
operator|new
name|Timer
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|t
operator|.
name|tick
argument_list|(
literal|"modBenchmarks()"
argument_list|)
expr_stmt|;
specifier|final
name|long
index|[]
index|[]
index|[]
name|en
init|=
name|generateEN
argument_list|(
literal|10000
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|t
operator|.
name|tick
argument_list|(
literal|"generateEN"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|en
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|n
init|=
name|en
index|[
name|i
index|]
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|en
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|long
name|e
init|=
name|en
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|long
name|answer
init|=
name|en
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|long
name|s
init|=
name|Modular
operator|.
name|mod
argument_list|(
name|e
argument_list|,
name|n
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
name|answer
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"e="
operator|+
name|e
operator|+
literal|", n="
operator|+
name|n
operator|+
literal|", answer="
operator|+
name|answer
operator|+
literal|" but s="
operator|+
name|s
argument_list|,
name|answer
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|t
operator|.
name|tick
argument_list|(
literal|"Modular.mod"
argument_list|)
expr_stmt|;
specifier|final
name|Montgomery2
name|m2
init|=
operator|new
name|Montgomery2
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|en
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|n
init|=
name|en
index|[
name|i
index|]
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
name|m2
operator|.
name|set
argument_list|(
name|n
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|en
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|long
name|e
init|=
name|en
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|long
name|answer
init|=
name|en
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|long
name|s
init|=
name|m2
operator|.
name|mod
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
name|answer
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"e="
operator|+
name|e
operator|+
literal|", n="
operator|+
name|n
operator|+
literal|", answer="
operator|+
name|answer
operator|+
literal|" but s="
operator|+
name|s
argument_list|,
name|answer
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|t
operator|.
name|tick
argument_list|(
literal|"montgomery.mod"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|en
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|n
init|=
name|en
index|[
name|i
index|]
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
name|m2
operator|.
name|set
argument_list|(
name|n
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|en
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|long
name|e
init|=
name|en
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|long
name|answer
init|=
name|en
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|long
name|s
init|=
name|m2
operator|.
name|mod2
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
name|answer
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"e="
operator|+
name|e
operator|+
literal|", n="
operator|+
name|n
operator|+
literal|", answer="
operator|+
name|answer
operator|+
literal|" but s="
operator|+
name|s
argument_list|,
name|answer
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|t
operator|.
name|tick
argument_list|(
literal|"montgomery.mod2"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|en
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|n
init|=
name|en
index|[
name|i
index|]
index|[
literal|0
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|BigInteger
name|N
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|n
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|en
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|long
name|e
init|=
name|en
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|long
name|answer
init|=
name|en
index|[
name|i
index|]
index|[
name|j
index|]
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|long
name|s
init|=
name|TWO
operator|.
name|modPow
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|e
argument_list|)
argument_list|,
name|N
argument_list|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|!=
name|answer
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"e="
operator|+
name|e
operator|+
literal|", n="
operator|+
name|n
operator|+
literal|", answer="
operator|+
name|answer
operator|+
literal|" but s="
operator|+
name|s
argument_list|,
name|answer
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|t
operator|.
name|tick
argument_list|(
literal|"BigInteger.modPow(e, n)"
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|squareBenchmarks
argument_list|()
expr_stmt|;
name|modBenchmarks
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

