begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.lib.db
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
name|db
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

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
name|mapreduce
operator|.
name|InputSplit
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

begin_comment
comment|/**  * Implement DBSplitter over text strings.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|TextSplitter
specifier|public
class|class
name|TextSplitter
extends|extends
name|BigDecimalSplitter
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TextSplitter
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * This method needs to determine the splits between two user-provided strings.    * In the case where the user's strings are 'A' and 'Z', this is not hard; we     * could create two splits from ['A', 'M') and ['M', 'Z'], 26 splits for strings    * beginning with each letter, etc.    *    * If a user has provided us with the strings "Ham" and "Haze", however, we need    * to create splits that differ in the third letter.    *    * The algorithm used is as follows:    * Since there are 2**16 unicode characters, we interpret characters as digits in    * base 65536. Given a string 's' containing characters s_0, s_1 .. s_n, we interpret    * the string as the number: 0.s_0 s_1 s_2.. s_n in base 65536. Having mapped the    * low and high strings into floating-point values, we then use the BigDecimalSplitter    * to establish the even split points, then map the resulting floating point values    * back into strings.    */
DECL|method|split (Configuration conf, ResultSet results, String colName)
specifier|public
name|List
argument_list|<
name|InputSplit
argument_list|>
name|split
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ResultSet
name|results
parameter_list|,
name|String
name|colName
parameter_list|)
throws|throws
name|SQLException
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Generating splits for a textual index column."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"If your database sorts in a case-insensitive order, "
operator|+
literal|"this may result in a partial import or duplicate records."
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"You are strongly encouraged to choose an integral split column."
argument_list|)
expr_stmt|;
name|String
name|minString
init|=
name|results
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|String
name|maxString
init|=
name|results
operator|.
name|getString
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|boolean
name|minIsNull
init|=
literal|false
decl_stmt|;
comment|// If the min value is null, switch it to an empty string instead for purposes
comment|// of interpolation. Then add [null, null] as a special case split.
if|if
condition|(
literal|null
operator|==
name|minString
condition|)
block|{
name|minString
operator|=
literal|""
expr_stmt|;
name|minIsNull
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|==
name|maxString
condition|)
block|{
comment|// If the max string is null, then the min string has to be null too.
comment|// Just return a special split for this case.
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
init|=
operator|new
name|ArrayList
argument_list|<
name|InputSplit
argument_list|>
argument_list|()
decl_stmt|;
name|splits
operator|.
name|add
argument_list|(
operator|new
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
argument_list|(
name|colName
operator|+
literal|" IS NULL"
argument_list|,
name|colName
operator|+
literal|" IS NULL"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|splits
return|;
block|}
comment|// Use this as a hint. May need an extra task if the size doesn't
comment|// divide cleanly.
name|int
name|numSplits
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|NUM_MAPS
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|lowClausePrefix
init|=
name|colName
operator|+
literal|">= '"
decl_stmt|;
name|String
name|highClausePrefix
init|=
name|colName
operator|+
literal|"< '"
decl_stmt|;
comment|// If there is a common prefix between minString and maxString, establish it
comment|// and pull it out of minString and maxString.
name|int
name|maxPrefixLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|minString
operator|.
name|length
argument_list|()
argument_list|,
name|maxString
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|sharedLen
decl_stmt|;
for|for
control|(
name|sharedLen
operator|=
literal|0
init|;
name|sharedLen
operator|<
name|maxPrefixLen
condition|;
name|sharedLen
operator|++
control|)
block|{
name|char
name|c1
init|=
name|minString
operator|.
name|charAt
argument_list|(
name|sharedLen
argument_list|)
decl_stmt|;
name|char
name|c2
init|=
name|maxString
operator|.
name|charAt
argument_list|(
name|sharedLen
argument_list|)
decl_stmt|;
if|if
condition|(
name|c1
operator|!=
name|c2
condition|)
block|{
break|break;
block|}
block|}
comment|// The common prefix has length 'sharedLen'. Extract it from both.
name|String
name|commonPrefix
init|=
name|minString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sharedLen
argument_list|)
decl_stmt|;
name|minString
operator|=
name|minString
operator|.
name|substring
argument_list|(
name|sharedLen
argument_list|)
expr_stmt|;
name|maxString
operator|=
name|maxString
operator|.
name|substring
argument_list|(
name|sharedLen
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|splitStrings
init|=
name|split
argument_list|(
name|numSplits
argument_list|,
name|minString
argument_list|,
name|maxString
argument_list|,
name|commonPrefix
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|InputSplit
argument_list|>
name|splits
init|=
operator|new
name|ArrayList
argument_list|<
name|InputSplit
argument_list|>
argument_list|()
decl_stmt|;
comment|// Convert the list of split point strings into an actual set of InputSplits.
name|String
name|start
init|=
name|splitStrings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|splitStrings
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|end
init|=
name|splitStrings
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|splitStrings
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
comment|// This is the last one; use a closed interval.
name|splits
operator|.
name|add
argument_list|(
operator|new
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
argument_list|(
name|lowClausePrefix
operator|+
name|start
operator|+
literal|"'"
argument_list|,
name|colName
operator|+
literal|"<= '"
operator|+
name|end
operator|+
literal|"'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Normal open-interval case.
name|splits
operator|.
name|add
argument_list|(
operator|new
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
argument_list|(
name|lowClausePrefix
operator|+
name|start
operator|+
literal|"'"
argument_list|,
name|highClausePrefix
operator|+
name|end
operator|+
literal|"'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|minIsNull
condition|)
block|{
comment|// Add the special null split at the end.
name|splits
operator|.
name|add
argument_list|(
operator|new
name|DataDrivenDBInputFormat
operator|.
name|DataDrivenDBInputSplit
argument_list|(
name|colName
operator|+
literal|" IS NULL"
argument_list|,
name|colName
operator|+
literal|" IS NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
DECL|method|split (int numSplits, String minString, String maxString, String commonPrefix)
name|List
argument_list|<
name|String
argument_list|>
name|split
parameter_list|(
name|int
name|numSplits
parameter_list|,
name|String
name|minString
parameter_list|,
name|String
name|maxString
parameter_list|,
name|String
name|commonPrefix
parameter_list|)
throws|throws
name|SQLException
block|{
name|BigDecimal
name|minVal
init|=
name|stringToBigDecimal
argument_list|(
name|minString
argument_list|)
decl_stmt|;
name|BigDecimal
name|maxVal
init|=
name|stringToBigDecimal
argument_list|(
name|maxString
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BigDecimal
argument_list|>
name|splitPoints
init|=
name|split
argument_list|(
operator|new
name|BigDecimal
argument_list|(
name|numSplits
argument_list|)
argument_list|,
name|minVal
argument_list|,
name|maxVal
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|splitStrings
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// Convert the BigDecimal splitPoints into their string representations.
for|for
control|(
name|BigDecimal
name|bd
range|:
name|splitPoints
control|)
block|{
name|splitStrings
operator|.
name|add
argument_list|(
name|commonPrefix
operator|+
name|bigDecimalToString
argument_list|(
name|bd
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Make sure that our user-specified boundaries are the first and last entries
comment|// in the array.
if|if
condition|(
name|splitStrings
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|||
operator|!
name|splitStrings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|commonPrefix
operator|+
name|minString
argument_list|)
condition|)
block|{
name|splitStrings
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|commonPrefix
operator|+
name|minString
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|splitStrings
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|||
operator|!
name|splitStrings
operator|.
name|get
argument_list|(
name|splitStrings
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|commonPrefix
operator|+
name|maxString
argument_list|)
condition|)
block|{
name|splitStrings
operator|.
name|add
argument_list|(
name|commonPrefix
operator|+
name|maxString
argument_list|)
expr_stmt|;
block|}
return|return
name|splitStrings
return|;
block|}
DECL|field|ONE_PLACE
specifier|private
specifier|final
specifier|static
name|BigDecimal
name|ONE_PLACE
init|=
operator|new
name|BigDecimal
argument_list|(
literal|65536
argument_list|)
decl_stmt|;
comment|// Maximum number of characters to convert. This is to prevent rounding errors
comment|// or repeating fractions near the very bottom from getting out of control. Note
comment|// that this still gives us a huge number of possible splits.
DECL|field|MAX_CHARS
specifier|private
specifier|final
specifier|static
name|int
name|MAX_CHARS
init|=
literal|8
decl_stmt|;
comment|/**    * Return a BigDecimal representation of string 'str' suitable for use    * in a numerically-sorting order.    */
DECL|method|stringToBigDecimal (String str)
name|BigDecimal
name|stringToBigDecimal
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|BigDecimal
name|result
init|=
name|BigDecimal
operator|.
name|ZERO
decl_stmt|;
name|BigDecimal
name|curPlace
init|=
name|ONE_PLACE
decl_stmt|;
comment|// start with 1/65536 to compute the first digit.
name|int
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
name|str
operator|.
name|length
argument_list|()
argument_list|,
name|MAX_CHARS
argument_list|)
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|codePoint
init|=
name|str
operator|.
name|codePointAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|result
operator|=
name|result
operator|.
name|add
argument_list|(
name|tryDivide
argument_list|(
operator|new
name|BigDecimal
argument_list|(
name|codePoint
argument_list|)
argument_list|,
name|curPlace
argument_list|)
argument_list|)
expr_stmt|;
comment|// advance to the next less significant place. e.g., 1/(65536^2) for the second char.
name|curPlace
operator|=
name|curPlace
operator|.
name|multiply
argument_list|(
name|ONE_PLACE
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Return the string encoded in a BigDecimal.    * Repeatedly multiply the input value by 65536; the integer portion after such a multiplication    * represents a single character in base 65536. Convert that back into a char and create a    * string out of these until we have no data left.    */
DECL|method|bigDecimalToString (BigDecimal bd)
name|String
name|bigDecimalToString
parameter_list|(
name|BigDecimal
name|bd
parameter_list|)
block|{
name|BigDecimal
name|cur
init|=
name|bd
operator|.
name|stripTrailingZeros
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|numConverted
init|=
literal|0
init|;
name|numConverted
operator|<
name|MAX_CHARS
condition|;
name|numConverted
operator|++
control|)
block|{
name|cur
operator|=
name|cur
operator|.
name|multiply
argument_list|(
name|ONE_PLACE
argument_list|)
expr_stmt|;
name|int
name|curCodePoint
init|=
name|cur
operator|.
name|intValue
argument_list|()
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|curCodePoint
condition|)
block|{
break|break;
block|}
name|cur
operator|=
name|cur
operator|.
name|subtract
argument_list|(
operator|new
name|BigDecimal
argument_list|(
name|curCodePoint
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Character
operator|.
name|toChars
argument_list|(
name|curCodePoint
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

