begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.partition
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|lib
operator|.
name|partition
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configurable
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
name|conf
operator|.
name|Configuration
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
name|io
operator|.
name|WritableComparator
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
name|io
operator|.
name|WritableUtils
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
name|io
operator|.
name|Text
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
name|mapreduce
operator|.
name|Job
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
name|mapreduce
operator|.
name|JobContext
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|lib
operator|.
name|partition
operator|.
name|KeyFieldHelper
operator|.
name|KeyDescription
import|;
end_import

begin_comment
comment|/**  * This comparator implementation provides a subset of the features provided  * by the Unix/GNU Sort. In particular, the supported features are:  * -n, (Sort numerically)  * -r, (Reverse the result of comparison)  * -k pos1[,pos2], where pos is of the form f[.c][opts], where f is the number  *  of the field to use, and c is the number of the first character from the  *  beginning of the field. Fields and character posns are numbered starting  *  with 1; a character position of zero in pos2 indicates the field's last  *  character. If '.c' is omitted from pos1, it defaults to 1 (the beginning  *  of the field); if omitted from pos2, it defaults to 0 (the end of the  *  field). opts are ordering options (any of 'nr' as described above).   * We assume that the fields in the key are separated by   * {@link JobContext#MAP_OUTPUT_KEY_FIELD_SEPARATOR}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|KeyFieldBasedComparator
specifier|public
class|class
name|KeyFieldBasedComparator
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|WritableComparator
implements|implements
name|Configurable
block|{
DECL|field|keyFieldHelper
specifier|private
name|KeyFieldHelper
name|keyFieldHelper
init|=
operator|new
name|KeyFieldHelper
argument_list|()
decl_stmt|;
DECL|field|COMPARATOR_OPTIONS
specifier|public
specifier|static
name|String
name|COMPARATOR_OPTIONS
init|=
literal|"mapreduce.partition.keycomparator.options"
decl_stmt|;
DECL|field|NEGATIVE
specifier|private
specifier|static
specifier|final
name|byte
name|NEGATIVE
init|=
operator|(
name|byte
operator|)
literal|'-'
decl_stmt|;
DECL|field|ZERO
specifier|private
specifier|static
specifier|final
name|byte
name|ZERO
init|=
operator|(
name|byte
operator|)
literal|'0'
decl_stmt|;
DECL|field|DECIMAL
specifier|private
specifier|static
specifier|final
name|byte
name|DECIMAL
init|=
operator|(
name|byte
operator|)
literal|'.'
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|String
name|option
init|=
name|conf
operator|.
name|get
argument_list|(
name|COMPARATOR_OPTIONS
argument_list|)
decl_stmt|;
name|String
name|keyFieldSeparator
init|=
name|conf
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|MAP_OUTPUT_KEY_FIELD_SEPARATOR
argument_list|,
literal|"\t"
argument_list|)
decl_stmt|;
name|keyFieldHelper
operator|.
name|setKeyFieldSeparator
argument_list|(
name|keyFieldSeparator
argument_list|)
expr_stmt|;
name|keyFieldHelper
operator|.
name|parseOption
argument_list|(
name|option
argument_list|)
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
DECL|method|KeyFieldBasedComparator ()
specifier|public
name|KeyFieldBasedComparator
parameter_list|()
block|{
name|super
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|compare (byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
specifier|public
name|int
name|compare
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
name|int
name|n1
init|=
name|WritableUtils
operator|.
name|decodeVIntSize
argument_list|(
name|b1
index|[
name|s1
index|]
argument_list|)
decl_stmt|;
name|int
name|n2
init|=
name|WritableUtils
operator|.
name|decodeVIntSize
argument_list|(
name|b2
index|[
name|s2
index|]
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|KeyDescription
argument_list|>
name|allKeySpecs
init|=
name|keyFieldHelper
operator|.
name|keySpecs
argument_list|()
decl_stmt|;
if|if
condition|(
name|allKeySpecs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|compareBytes
argument_list|(
name|b1
argument_list|,
name|s1
operator|+
name|n1
argument_list|,
name|l1
operator|-
name|n1
argument_list|,
name|b2
argument_list|,
name|s2
operator|+
name|n2
argument_list|,
name|l2
operator|-
name|n2
argument_list|)
return|;
block|}
name|int
index|[]
name|lengthIndicesFirst
init|=
name|keyFieldHelper
operator|.
name|getWordLengths
argument_list|(
name|b1
argument_list|,
name|s1
operator|+
name|n1
argument_list|,
name|s1
operator|+
name|l1
argument_list|)
decl_stmt|;
name|int
index|[]
name|lengthIndicesSecond
init|=
name|keyFieldHelper
operator|.
name|getWordLengths
argument_list|(
name|b2
argument_list|,
name|s2
operator|+
name|n2
argument_list|,
name|s2
operator|+
name|l2
argument_list|)
decl_stmt|;
for|for
control|(
name|KeyDescription
name|keySpec
range|:
name|allKeySpecs
control|)
block|{
name|int
name|startCharFirst
init|=
name|keyFieldHelper
operator|.
name|getStartOffset
argument_list|(
name|b1
argument_list|,
name|s1
operator|+
name|n1
argument_list|,
name|s1
operator|+
name|l1
argument_list|,
name|lengthIndicesFirst
argument_list|,
name|keySpec
argument_list|)
decl_stmt|;
name|int
name|endCharFirst
init|=
name|keyFieldHelper
operator|.
name|getEndOffset
argument_list|(
name|b1
argument_list|,
name|s1
operator|+
name|n1
argument_list|,
name|s1
operator|+
name|l1
argument_list|,
name|lengthIndicesFirst
argument_list|,
name|keySpec
argument_list|)
decl_stmt|;
name|int
name|startCharSecond
init|=
name|keyFieldHelper
operator|.
name|getStartOffset
argument_list|(
name|b2
argument_list|,
name|s2
operator|+
name|n2
argument_list|,
name|s2
operator|+
name|l2
argument_list|,
name|lengthIndicesSecond
argument_list|,
name|keySpec
argument_list|)
decl_stmt|;
name|int
name|endCharSecond
init|=
name|keyFieldHelper
operator|.
name|getEndOffset
argument_list|(
name|b2
argument_list|,
name|s2
operator|+
name|n2
argument_list|,
name|s2
operator|+
name|l2
argument_list|,
name|lengthIndicesSecond
argument_list|,
name|keySpec
argument_list|)
decl_stmt|;
name|int
name|result
decl_stmt|;
if|if
condition|(
operator|(
name|result
operator|=
name|compareByteSequence
argument_list|(
name|b1
argument_list|,
name|startCharFirst
argument_list|,
name|endCharFirst
argument_list|,
name|b2
argument_list|,
name|startCharSecond
argument_list|,
name|endCharSecond
argument_list|,
name|keySpec
argument_list|)
operator|)
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
literal|0
return|;
block|}
DECL|method|compareByteSequence (byte[] first, int start1, int end1, byte[] second, int start2, int end2, KeyDescription key)
specifier|private
name|int
name|compareByteSequence
parameter_list|(
name|byte
index|[]
name|first
parameter_list|,
name|int
name|start1
parameter_list|,
name|int
name|end1
parameter_list|,
name|byte
index|[]
name|second
parameter_list|,
name|int
name|start2
parameter_list|,
name|int
name|end2
parameter_list|,
name|KeyDescription
name|key
parameter_list|)
block|{
if|if
condition|(
name|start1
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|key
operator|.
name|reverse
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|start2
operator|==
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|key
operator|.
name|reverse
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|1
return|;
block|}
name|int
name|compareResult
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|key
operator|.
name|numeric
condition|)
block|{
name|compareResult
operator|=
name|compareBytes
argument_list|(
name|first
argument_list|,
name|start1
argument_list|,
name|end1
operator|-
name|start1
operator|+
literal|1
argument_list|,
name|second
argument_list|,
name|start2
argument_list|,
name|end2
operator|-
name|start2
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|.
name|numeric
condition|)
block|{
name|compareResult
operator|=
name|numericalCompare
argument_list|(
name|first
argument_list|,
name|start1
argument_list|,
name|end1
argument_list|,
name|second
argument_list|,
name|start2
argument_list|,
name|end2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|key
operator|.
name|reverse
condition|)
block|{
return|return
operator|-
name|compareResult
return|;
block|}
return|return
name|compareResult
return|;
block|}
DECL|method|numericalCompare (byte[] a, int start1, int end1, byte[] b, int start2, int end2)
specifier|private
name|int
name|numericalCompare
parameter_list|(
name|byte
index|[]
name|a
parameter_list|,
name|int
name|start1
parameter_list|,
name|int
name|end1
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|start2
parameter_list|,
name|int
name|end2
parameter_list|)
block|{
name|int
name|i
init|=
name|start1
decl_stmt|;
name|int
name|j
init|=
name|start2
decl_stmt|;
name|int
name|mul
init|=
literal|1
decl_stmt|;
name|byte
name|first_a
init|=
name|a
index|[
name|i
index|]
decl_stmt|;
name|byte
name|first_b
init|=
name|b
index|[
name|j
index|]
decl_stmt|;
if|if
condition|(
name|first_a
operator|==
name|NEGATIVE
condition|)
block|{
if|if
condition|(
name|first_b
operator|!=
name|NEGATIVE
condition|)
block|{
comment|//check for cases like -0.0 and 0.0 (they should be declared equal)
return|return
name|oneNegativeCompare
argument_list|(
name|a
argument_list|,
name|start1
operator|+
literal|1
argument_list|,
name|end1
argument_list|,
name|b
argument_list|,
name|start2
argument_list|,
name|end2
argument_list|)
return|;
block|}
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|first_b
operator|==
name|NEGATIVE
condition|)
block|{
if|if
condition|(
name|first_a
operator|!=
name|NEGATIVE
condition|)
block|{
comment|//check for cases like 0.0 and -0.0 (they should be declared equal)
return|return
operator|-
name|oneNegativeCompare
argument_list|(
name|b
argument_list|,
name|start2
operator|+
literal|1
argument_list|,
name|end2
argument_list|,
name|a
argument_list|,
name|start1
argument_list|,
name|end1
argument_list|)
return|;
block|}
name|j
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|first_b
operator|==
name|NEGATIVE
operator|&&
name|first_a
operator|==
name|NEGATIVE
condition|)
block|{
name|mul
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|//skip over ZEROs
while|while
condition|(
name|i
operator|<=
name|end1
condition|)
block|{
if|if
condition|(
name|a
index|[
name|i
index|]
operator|!=
name|ZERO
condition|)
block|{
break|break;
block|}
name|i
operator|++
expr_stmt|;
block|}
while|while
condition|(
name|j
operator|<=
name|end2
condition|)
block|{
if|if
condition|(
name|b
index|[
name|j
index|]
operator|!=
name|ZERO
condition|)
block|{
break|break;
block|}
name|j
operator|++
expr_stmt|;
block|}
comment|//skip over equal characters and stopping at the first nondigit char
comment|//The nondigit character could be '.'
while|while
condition|(
name|i
operator|<=
name|end1
operator|&&
name|j
operator|<=
name|end2
condition|)
block|{
if|if
condition|(
operator|!
name|isdigit
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
operator|||
name|a
index|[
name|i
index|]
operator|!=
name|b
index|[
name|j
index|]
condition|)
block|{
break|break;
block|}
name|i
operator|++
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|<=
name|end1
condition|)
block|{
name|first_a
operator|=
name|a
index|[
name|i
index|]
expr_stmt|;
block|}
if|if
condition|(
name|j
operator|<=
name|end2
condition|)
block|{
name|first_b
operator|=
name|b
index|[
name|j
index|]
expr_stmt|;
block|}
comment|//store the result of the difference. This could be final result if the
comment|//number of digits in the mantissa is the same in both the numbers
name|int
name|firstResult
init|=
name|first_a
operator|-
name|first_b
decl_stmt|;
comment|//check whether we hit a decimal in the earlier scan
if|if
condition|(
operator|(
name|first_a
operator|==
name|DECIMAL
operator|&&
operator|(
operator|!
name|isdigit
argument_list|(
name|first_b
argument_list|)
operator|||
name|j
operator|>
name|end2
operator|)
operator|)
operator|||
operator|(
name|first_b
operator|==
name|DECIMAL
operator|&&
operator|(
operator|!
name|isdigit
argument_list|(
name|first_a
argument_list|)
operator|||
name|i
operator|>
name|end1
operator|)
operator|)
condition|)
block|{
return|return
operator|(
operator|(
name|mul
operator|<
literal|0
operator|)
condition|?
operator|-
name|decimalCompare
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|end1
argument_list|,
name|b
argument_list|,
name|j
argument_list|,
name|end2
argument_list|)
else|:
name|decimalCompare
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|end1
argument_list|,
name|b
argument_list|,
name|j
argument_list|,
name|end2
argument_list|)
operator|)
return|;
block|}
comment|//check the number of digits in the mantissa of the numbers
name|int
name|numRemainDigits_a
init|=
literal|0
decl_stmt|;
name|int
name|numRemainDigits_b
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<=
name|end1
condition|)
block|{
comment|//if we encounter a non-digit treat the corresponding number as being
comment|//smaller
if|if
condition|(
name|isdigit
argument_list|(
name|a
index|[
name|i
operator|++
index|]
argument_list|)
condition|)
block|{
name|numRemainDigits_a
operator|++
expr_stmt|;
block|}
else|else
break|break;
block|}
while|while
condition|(
name|j
operator|<=
name|end2
condition|)
block|{
comment|//if we encounter a non-digit treat the corresponding number as being
comment|//smaller
if|if
condition|(
name|isdigit
argument_list|(
name|b
index|[
name|j
operator|++
index|]
argument_list|)
condition|)
block|{
name|numRemainDigits_b
operator|++
expr_stmt|;
block|}
else|else
break|break;
block|}
name|int
name|ret
init|=
name|numRemainDigits_a
operator|-
name|numRemainDigits_b
decl_stmt|;
if|if
condition|(
name|ret
operator|==
literal|0
condition|)
block|{
return|return
operator|(
operator|(
name|mul
operator|<
literal|0
operator|)
condition|?
operator|-
name|firstResult
else|:
name|firstResult
operator|)
return|;
block|}
else|else
block|{
return|return
operator|(
operator|(
name|mul
operator|<
literal|0
operator|)
condition|?
operator|-
name|ret
else|:
name|ret
operator|)
return|;
block|}
block|}
DECL|method|isdigit (byte b)
specifier|private
name|boolean
name|isdigit
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
if|if
condition|(
literal|'0'
operator|<=
name|b
operator|&&
name|b
operator|<=
literal|'9'
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|decimalCompare (byte[] a, int i, int end1, byte[] b, int j, int end2)
specifier|private
name|int
name|decimalCompare
parameter_list|(
name|byte
index|[]
name|a
parameter_list|,
name|int
name|i
parameter_list|,
name|int
name|end1
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|j
parameter_list|,
name|int
name|end2
parameter_list|)
block|{
if|if
condition|(
name|i
operator|>
name|end1
condition|)
block|{
comment|//if a[] has nothing remaining
return|return
operator|-
name|decimalCompare1
argument_list|(
name|b
argument_list|,
operator|++
name|j
argument_list|,
name|end2
argument_list|)
return|;
block|}
if|if
condition|(
name|j
operator|>
name|end2
condition|)
block|{
comment|//if b[] has nothing remaining
return|return
name|decimalCompare1
argument_list|(
name|a
argument_list|,
operator|++
name|i
argument_list|,
name|end1
argument_list|)
return|;
block|}
if|if
condition|(
name|a
index|[
name|i
index|]
operator|==
name|DECIMAL
operator|&&
name|b
index|[
name|j
index|]
operator|==
name|DECIMAL
condition|)
block|{
while|while
condition|(
name|i
operator|<=
name|end1
operator|&&
name|j
operator|<=
name|end2
condition|)
block|{
if|if
condition|(
name|a
index|[
name|i
index|]
operator|!=
name|b
index|[
name|j
index|]
condition|)
block|{
if|if
condition|(
name|isdigit
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
operator|&&
name|isdigit
argument_list|(
name|b
index|[
name|j
index|]
argument_list|)
condition|)
block|{
return|return
name|a
index|[
name|i
index|]
operator|-
name|b
index|[
name|j
index|]
return|;
block|}
if|if
condition|(
name|isdigit
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|isdigit
argument_list|(
name|b
index|[
name|j
index|]
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
name|i
operator|++
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>
name|end1
operator|&&
name|j
operator|>
name|end2
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|i
operator|>
name|end1
condition|)
block|{
comment|//check whether there is a non-ZERO digit after potentially
comment|//a number of ZEROs (e.g., a=.4444, b=.444400004)
return|return
operator|-
name|decimalCompare1
argument_list|(
name|b
argument_list|,
name|j
argument_list|,
name|end2
argument_list|)
return|;
block|}
if|if
condition|(
name|j
operator|>
name|end2
condition|)
block|{
comment|//check whether there is a non-ZERO digit after potentially
comment|//a number of ZEROs (e.g., b=.4444, a=.444400004)
return|return
name|decimalCompare1
argument_list|(
name|a
argument_list|,
name|i
argument_list|,
name|end1
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|a
index|[
name|i
index|]
operator|==
name|DECIMAL
condition|)
block|{
return|return
name|decimalCompare1
argument_list|(
name|a
argument_list|,
operator|++
name|i
argument_list|,
name|end1
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|b
index|[
name|j
index|]
operator|==
name|DECIMAL
condition|)
block|{
return|return
operator|-
name|decimalCompare1
argument_list|(
name|b
argument_list|,
operator|++
name|j
argument_list|,
name|end2
argument_list|)
return|;
block|}
return|return
literal|0
return|;
block|}
DECL|method|decimalCompare1 (byte[] a, int i, int end)
specifier|private
name|int
name|decimalCompare1
parameter_list|(
name|byte
index|[]
name|a
parameter_list|,
name|int
name|i
parameter_list|,
name|int
name|end
parameter_list|)
block|{
while|while
condition|(
name|i
operator|<=
name|end
condition|)
block|{
if|if
condition|(
name|a
index|[
name|i
index|]
operator|==
name|ZERO
condition|)
block|{
name|i
operator|++
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|isdigit
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
DECL|method|oneNegativeCompare (byte[] a, int start1, int end1, byte[] b, int start2, int end2)
specifier|private
name|int
name|oneNegativeCompare
parameter_list|(
name|byte
index|[]
name|a
parameter_list|,
name|int
name|start1
parameter_list|,
name|int
name|end1
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|start2
parameter_list|,
name|int
name|end2
parameter_list|)
block|{
comment|//here a[] is negative and b[] is positive
comment|//We have to ascertain whether the number contains any digits.
comment|//If it does, then it is a smaller number for sure. If not,
comment|//then we need to scan b[] to find out whether b[] has a digit
comment|//If b[] does contain a digit, then b[] is certainly
comment|//greater. If not, that is, both a[] and b[] don't contain
comment|//digits then they should be considered equal.
if|if
condition|(
operator|!
name|isZero
argument_list|(
name|a
argument_list|,
name|start1
argument_list|,
name|end1
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|//reached here - this means that a[] is a ZERO
if|if
condition|(
operator|!
name|isZero
argument_list|(
name|b
argument_list|,
name|start2
argument_list|,
name|end2
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
comment|//reached here - both numbers are basically ZEROs and hence
comment|//they should compare equal
return|return
literal|0
return|;
block|}
DECL|method|isZero (byte a[], int start, int end)
specifier|private
name|boolean
name|isZero
parameter_list|(
name|byte
name|a
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
comment|//check for zeros in the significand part as well as the decimal part
comment|//note that we treat the non-digit characters as ZERO
name|int
name|i
init|=
name|start
decl_stmt|;
comment|//we check the significand for being a ZERO
while|while
condition|(
name|i
operator|<=
name|end
condition|)
block|{
if|if
condition|(
name|a
index|[
name|i
index|]
operator|!=
name|ZERO
condition|)
block|{
if|if
condition|(
name|a
index|[
name|i
index|]
operator|!=
name|DECIMAL
operator|&&
name|isdigit
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
block|}
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|!=
operator|(
name|end
operator|+
literal|1
operator|)
operator|&&
name|a
index|[
name|i
operator|++
index|]
operator|==
name|DECIMAL
condition|)
block|{
comment|//we check the decimal part for being a ZERO
while|while
condition|(
name|i
operator|<=
name|end
condition|)
block|{
if|if
condition|(
name|a
index|[
name|i
index|]
operator|!=
name|ZERO
condition|)
block|{
if|if
condition|(
name|isdigit
argument_list|(
name|a
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Set the {@link KeyFieldBasedComparator} options used to compare keys.    *     * @param keySpec the key specification of the form -k pos1[,pos2], where,    *  pos is of the form f[.c][opts], where f is the number    *  of the key field to use, and c is the number of the first character from    *  the beginning of the field. Fields and character posns are numbered     *  starting with 1; a character position of zero in pos2 indicates the    *  field's last character. If '.c' is omitted from pos1, it defaults to 1    *  (the beginning of the field); if omitted from pos2, it defaults to 0     *  (the end of the field). opts are ordering options. The supported options    *  are:    *    -n, (Sort numerically)    *    -r, (Reverse the result of comparison)                     */
DECL|method|setKeyFieldComparatorOptions (Job job, String keySpec)
specifier|public
specifier|static
name|void
name|setKeyFieldComparatorOptions
parameter_list|(
name|Job
name|job
parameter_list|,
name|String
name|keySpec
parameter_list|)
block|{
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|set
argument_list|(
name|COMPARATOR_OPTIONS
argument_list|,
name|keySpec
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the {@link KeyFieldBasedComparator} options    */
DECL|method|getKeyFieldComparatorOption (JobContext job)
specifier|public
specifier|static
name|String
name|getKeyFieldComparatorOption
parameter_list|(
name|JobContext
name|job
parameter_list|)
block|{
return|return
name|job
operator|.
name|getConfiguration
argument_list|()
operator|.
name|get
argument_list|(
name|COMPARATOR_OPTIONS
argument_list|)
return|;
block|}
block|}
end_class

end_unit

