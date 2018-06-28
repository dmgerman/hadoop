begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteOrder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessController
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|sun
operator|.
name|misc
operator|.
name|Unsafe
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|UnsignedBytes
import|;
end_import

begin_comment
comment|/**  * Utility code to do optimized byte-array comparison.  * This is borrowed and slightly modified from Guava's {@link UnsignedBytes}  * class to be able to compare arrays that start at non-zero offsets.  */
end_comment

begin_class
DECL|class|FastByteComparisons
specifier|abstract
class|class
name|FastByteComparisons
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FastByteComparisons
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Lexicographically compare two byte arrays.    */
DECL|method|compareTo (byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
specifier|public
specifier|static
name|int
name|compareTo
parameter_list|(
name|byte
index|[]
name|b1
parameter_list|,
name|int
name|s1
parameter_list|,
name|int
name|l1
parameter_list|,
name|byte
index|[]
name|b2
parameter_list|,
name|int
name|s2
parameter_list|,
name|int
name|l2
parameter_list|)
block|{
return|return
name|LexicographicalComparerHolder
operator|.
name|BEST_COMPARER
operator|.
name|compareTo
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|,
name|l1
argument_list|,
name|b2
argument_list|,
name|s2
argument_list|,
name|l2
argument_list|)
return|;
block|}
DECL|interface|Comparer
specifier|private
interface|interface
name|Comparer
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|compareTo (T buffer1, int offset1, int length1, T buffer2, int offset2, int length2)
specifier|abstract
specifier|public
name|int
name|compareTo
parameter_list|(
name|T
name|buffer1
parameter_list|,
name|int
name|offset1
parameter_list|,
name|int
name|length1
parameter_list|,
name|T
name|buffer2
parameter_list|,
name|int
name|offset2
parameter_list|,
name|int
name|length2
parameter_list|)
function_decl|;
block|}
DECL|method|lexicographicalComparerJavaImpl ()
specifier|private
specifier|static
name|Comparer
argument_list|<
name|byte
index|[]
argument_list|>
name|lexicographicalComparerJavaImpl
parameter_list|()
block|{
return|return
name|LexicographicalComparerHolder
operator|.
name|PureJavaComparer
operator|.
name|INSTANCE
return|;
block|}
comment|/**    * Provides a lexicographical comparer implementation; either a Java    * implementation or a faster implementation based on {@link Unsafe}.    *    *<p>Uses reflection to gracefully fall back to the Java implementation if    * {@code Unsafe} isn't available.    */
DECL|class|LexicographicalComparerHolder
specifier|private
specifier|static
class|class
name|LexicographicalComparerHolder
block|{
DECL|field|UNSAFE_COMPARER_NAME
specifier|static
specifier|final
name|String
name|UNSAFE_COMPARER_NAME
init|=
name|LexicographicalComparerHolder
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"$UnsafeComparer"
decl_stmt|;
DECL|field|BEST_COMPARER
specifier|static
specifier|final
name|Comparer
argument_list|<
name|byte
index|[]
argument_list|>
name|BEST_COMPARER
init|=
name|getBestComparer
argument_list|()
decl_stmt|;
comment|/**      * Returns the Unsafe-using Comparer, or falls back to the pure-Java      * implementation if unable to do so.      */
DECL|method|getBestComparer ()
specifier|static
name|Comparer
argument_list|<
name|byte
index|[]
argument_list|>
name|getBestComparer
parameter_list|()
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.arch"
argument_list|)
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"sparc"
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Lexicographical comparer selected for "
operator|+
literal|"byte aligned system architecture"
argument_list|)
expr_stmt|;
block|}
return|return
name|lexicographicalComparerJavaImpl
argument_list|()
return|;
block|}
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|theClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|UNSAFE_COMPARER_NAME
argument_list|)
decl_stmt|;
comment|// yes, UnsafeComparer does implement Comparer<byte[]>
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Comparer
argument_list|<
name|byte
index|[]
argument_list|>
name|comparer
init|=
operator|(
name|Comparer
argument_list|<
name|byte
index|[]
argument_list|>
operator|)
name|theClass
operator|.
name|getEnumConstants
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Unsafe comparer selected for "
operator|+
literal|"byte unaligned system architecture"
argument_list|)
expr_stmt|;
block|}
return|return
name|comparer
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// ensure we really catch *everything*
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Lexicographical comparer selected"
argument_list|)
expr_stmt|;
block|}
return|return
name|lexicographicalComparerJavaImpl
argument_list|()
return|;
block|}
block|}
DECL|enum|PureJavaComparer
specifier|private
enum|enum
name|PureJavaComparer
implements|implements
name|Comparer
argument_list|<
name|byte
index|[]
argument_list|>
block|{
DECL|enumConstant|INSTANCE
name|INSTANCE
block|;
annotation|@
name|Override
DECL|method|compareTo (byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2)
specifier|public
name|int
name|compareTo
parameter_list|(
name|byte
index|[]
name|buffer1
parameter_list|,
name|int
name|offset1
parameter_list|,
name|int
name|length1
parameter_list|,
name|byte
index|[]
name|buffer2
parameter_list|,
name|int
name|offset2
parameter_list|,
name|int
name|length2
parameter_list|)
block|{
comment|// Short circuit equal case
if|if
condition|(
name|buffer1
operator|==
name|buffer2
operator|&&
name|offset1
operator|==
name|offset2
operator|&&
name|length1
operator|==
name|length2
condition|)
block|{
return|return
literal|0
return|;
block|}
comment|// Bring WritableComparator code local
name|int
name|end1
init|=
name|offset1
operator|+
name|length1
decl_stmt|;
name|int
name|end2
init|=
name|offset2
operator|+
name|length2
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset1
operator|,
name|j
operator|=
name|offset2
condition|;
name|i
operator|<
name|end1
operator|&&
name|j
operator|<
name|end2
incr|;
control|i++
operator|,
control|j++)
block|{
name|int
name|a
init|=
operator|(
name|buffer1
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
name|int
name|b
init|=
operator|(
name|buffer2
index|[
name|j
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
name|b
condition|)
block|{
return|return
name|a
operator|-
name|b
return|;
block|}
block|}
return|return
name|length1
operator|-
name|length2
return|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
comment|// used via reflection
DECL|enum|UnsafeComparer
specifier|private
enum|enum
name|UnsafeComparer
implements|implements
name|Comparer
argument_list|<
name|byte
index|[]
argument_list|>
block|{
DECL|enumConstant|INSTANCE
name|INSTANCE
block|;
DECL|field|theUnsafe
specifier|static
specifier|final
name|Unsafe
name|theUnsafe
decl_stmt|;
comment|/** The offset to the first element in a byte array. */
DECL|field|BYTE_ARRAY_BASE_OFFSET
specifier|static
specifier|final
name|int
name|BYTE_ARRAY_BASE_OFFSET
decl_stmt|;
static|static
block|{
name|theUnsafe
operator|=
operator|(
name|Unsafe
operator|)
name|AccessController
operator|.
name|doPrivileged
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
block|{
try|try
block|{
name|Field
name|f
init|=
name|Unsafe
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"theUnsafe"
argument_list|)
decl_stmt|;
name|f
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|f
operator|.
name|get
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchFieldException
name|e
parameter_list|)
block|{
comment|// It doesn't matter what we throw;
comment|// it's swallowed in getBestComparer().
throw|throw
operator|new
name|Error
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Error
argument_list|()
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|BYTE_ARRAY_BASE_OFFSET
operator|=
name|theUnsafe
operator|.
name|arrayBaseOffset
argument_list|(
name|byte
index|[]
operator|.
expr|class
argument_list|)
expr_stmt|;
comment|// sanity check - this should never fail
if|if
condition|(
name|theUnsafe
operator|.
name|arrayIndexScale
argument_list|(
name|byte
index|[]
operator|.
expr|class
argument_list|)
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
DECL|field|littleEndian
specifier|static
specifier|final
name|boolean
name|littleEndian
init|=
name|ByteOrder
operator|.
name|nativeOrder
argument_list|()
operator|.
name|equals
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
decl_stmt|;
comment|/**        * Returns true if x1 is less than x2, when both values are treated as        * unsigned.        */
DECL|method|lessThanUnsigned (long x1, long x2)
specifier|static
name|boolean
name|lessThanUnsigned
parameter_list|(
name|long
name|x1
parameter_list|,
name|long
name|x2
parameter_list|)
block|{
return|return
operator|(
name|x1
operator|+
name|Long
operator|.
name|MIN_VALUE
operator|)
operator|<
operator|(
name|x2
operator|+
name|Long
operator|.
name|MIN_VALUE
operator|)
return|;
block|}
comment|/**        * Lexicographically compare two arrays.        *        * @param buffer1 left operand        * @param buffer2 right operand        * @param offset1 Where to start comparing in the left buffer        * @param offset2 Where to start comparing in the right buffer        * @param length1 How much to compare from the left buffer        * @param length2 How much to compare from the right buffer        * @return 0 if equal,< 0 if left is less than right, etc.        */
annotation|@
name|Override
DECL|method|compareTo (byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2)
specifier|public
name|int
name|compareTo
parameter_list|(
name|byte
index|[]
name|buffer1
parameter_list|,
name|int
name|offset1
parameter_list|,
name|int
name|length1
parameter_list|,
name|byte
index|[]
name|buffer2
parameter_list|,
name|int
name|offset2
parameter_list|,
name|int
name|length2
parameter_list|)
block|{
comment|// Short circuit equal case
if|if
condition|(
name|buffer1
operator|==
name|buffer2
operator|&&
name|offset1
operator|==
name|offset2
operator|&&
name|length1
operator|==
name|length2
condition|)
block|{
return|return
literal|0
return|;
block|}
specifier|final
name|int
name|stride
init|=
literal|8
decl_stmt|;
name|int
name|minLength
init|=
name|Math
operator|.
name|min
argument_list|(
name|length1
argument_list|,
name|length2
argument_list|)
decl_stmt|;
name|int
name|strideLimit
init|=
name|minLength
operator|&
operator|~
operator|(
name|stride
operator|-
literal|1
operator|)
decl_stmt|;
name|int
name|offset1Adj
init|=
name|offset1
operator|+
name|BYTE_ARRAY_BASE_OFFSET
decl_stmt|;
name|int
name|offset2Adj
init|=
name|offset2
operator|+
name|BYTE_ARRAY_BASE_OFFSET
decl_stmt|;
name|int
name|i
decl_stmt|;
comment|/*          * Compare 8 bytes at a time. Benchmarking shows comparing 8 bytes at a          * time is no slower than comparing 4 bytes at a time even on 32-bit.          * On the other hand, it is substantially faster on 64-bit.          */
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|strideLimit
condition|;
name|i
operator|+=
name|stride
control|)
block|{
name|long
name|lw
init|=
name|theUnsafe
operator|.
name|getLong
argument_list|(
name|buffer1
argument_list|,
name|offset1Adj
operator|+
operator|(
name|long
operator|)
name|i
argument_list|)
decl_stmt|;
name|long
name|rw
init|=
name|theUnsafe
operator|.
name|getLong
argument_list|(
name|buffer2
argument_list|,
name|offset2Adj
operator|+
operator|(
name|long
operator|)
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|lw
operator|!=
name|rw
condition|)
block|{
if|if
condition|(
operator|!
name|littleEndian
condition|)
block|{
return|return
name|lessThanUnsigned
argument_list|(
name|lw
argument_list|,
name|rw
argument_list|)
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
comment|/*              * We want to compare only the first index where left[index] !=              * right[index]. This corresponds to the least significant nonzero              * byte in lw ^ rw, since lw and rw are little-endian.              * Long.numberOfTrailingZeros(diff) tells us the least significant              * nonzero bit, and zeroing out the first three bits of L.nTZ gives              * us the shift to get that least significant nonzero byte. This              * comparison logic is based on UnsignedBytes from Guava v21              */
name|int
name|n
init|=
name|Long
operator|.
name|numberOfTrailingZeros
argument_list|(
name|lw
operator|^
name|rw
argument_list|)
operator|&
operator|~
literal|0x7
decl_stmt|;
return|return
operator|(
call|(
name|int
call|)
argument_list|(
operator|(
name|lw
operator|>>>
name|n
operator|)
operator|&
literal|0xFF
argument_list|)
operator|)
operator|-
operator|(
call|(
name|int
call|)
argument_list|(
operator|(
name|rw
operator|>>>
name|n
operator|)
operator|&
literal|0xFF
argument_list|)
operator|)
return|;
block|}
block|}
comment|// The epilogue to cover the last (minLength % 8) elements.
for|for
control|(
init|;
name|i
operator|<
name|minLength
condition|;
name|i
operator|++
control|)
block|{
name|int
name|result
init|=
name|UnsignedBytes
operator|.
name|compare
argument_list|(
name|buffer1
index|[
name|offset1
operator|+
name|i
index|]
argument_list|,
name|buffer2
index|[
name|offset2
operator|+
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|0
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
return|return
name|length1
operator|-
name|length2
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

