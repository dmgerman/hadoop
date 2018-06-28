begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen.anonymization
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
operator|.
name|anonymization
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Utility class to handle commonly performed tasks in a   * {@link org.apache.hadoop.tools.rumen.datatypes.DefaultAnonymizableDataType}   * using a {@link WordList} for anonymization.  * //TODO There is no caching for saving memory.  */
end_comment

begin_class
DECL|class|WordListAnonymizerUtility
specifier|public
class|class
name|WordListAnonymizerUtility
block|{
DECL|field|KNOWN_WORDS
specifier|static
specifier|final
name|String
index|[]
name|KNOWN_WORDS
init|=
operator|new
name|String
index|[]
block|{
literal|"job"
block|,
literal|"tmp"
block|,
literal|"temp"
block|,
literal|"home"
block|,
literal|"homes"
block|,
literal|"usr"
block|,
literal|"user"
block|,
literal|"test"
block|}
decl_stmt|;
comment|/**    * Checks if the data needs anonymization. Typically, data types which are     * numeric in nature doesn't need anonymization.    */
DECL|method|needsAnonymization (String data)
specifier|public
specifier|static
name|boolean
name|needsAnonymization
parameter_list|(
name|String
name|data
parameter_list|)
block|{
comment|// Numeric data doesn't need anonymization
comment|// Currently this doesnt support inputs like
comment|//   - 12.3
comment|//   - 12.3f
comment|//   - 90L
comment|//   - 1D
if|if
condition|(
name|StringUtils
operator|.
name|isNumeric
argument_list|(
name|data
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
comment|// by default return true
block|}
comment|/**    * Checks if the given data has a known suffix.    */
DECL|method|hasSuffix (String data, String[] suffixes)
specifier|public
specifier|static
name|boolean
name|hasSuffix
parameter_list|(
name|String
name|data
parameter_list|,
name|String
index|[]
name|suffixes
parameter_list|)
block|{
comment|// check if they end in known suffixes
for|for
control|(
name|String
name|ks
range|:
name|suffixes
control|)
block|{
if|if
condition|(
name|data
operator|.
name|endsWith
argument_list|(
name|ks
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Extracts a known suffix from the given data.    *     * @throws RuntimeException if the data doesn't have a suffix.     *         Use {@link #hasSuffix(String, String[])} to make sure that the     *         given data has a suffix.    */
DECL|method|extractSuffix (String data, String[] suffixes)
specifier|public
specifier|static
name|String
index|[]
name|extractSuffix
parameter_list|(
name|String
name|data
parameter_list|,
name|String
index|[]
name|suffixes
parameter_list|)
block|{
comment|// check if they end in known suffixes
name|String
name|suffix
init|=
literal|""
decl_stmt|;
for|for
control|(
name|String
name|ks
range|:
name|suffixes
control|)
block|{
if|if
condition|(
name|data
operator|.
name|endsWith
argument_list|(
name|ks
argument_list|)
condition|)
block|{
name|suffix
operator|=
name|ks
expr_stmt|;
comment|// stripe off the suffix which will get appended later
name|data
operator|=
name|data
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|()
operator|-
name|suffix
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
index|[]
block|{
name|data
block|,
name|suffix
block|}
return|;
block|}
block|}
comment|// throw exception
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Data ["
operator|+
name|data
operator|+
literal|"] doesn't have a suffix from"
operator|+
literal|" known suffixes ["
operator|+
name|StringUtils
operator|.
name|join
argument_list|(
name|suffixes
argument_list|,
literal|','
argument_list|)
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|/**    * Checks if the given data is known. This API uses {@link #KNOWN_WORDS} to    * detect if the given data is a commonly used (so called 'known') word.    */
DECL|method|isKnownData (String data)
specifier|public
specifier|static
name|boolean
name|isKnownData
parameter_list|(
name|String
name|data
parameter_list|)
block|{
return|return
name|isKnownData
argument_list|(
name|data
argument_list|,
name|KNOWN_WORDS
argument_list|)
return|;
block|}
comment|/**    * Checks if the given data is known.    */
DECL|method|isKnownData (String data, String[] knownWords)
specifier|public
specifier|static
name|boolean
name|isKnownData
parameter_list|(
name|String
name|data
parameter_list|,
name|String
index|[]
name|knownWords
parameter_list|)
block|{
comment|// check if the data is known content
comment|//TODO [Chunking] Do this for sub-strings of data
for|for
control|(
name|String
name|kd
range|:
name|knownWords
control|)
block|{
if|if
condition|(
name|data
operator|.
name|equals
argument_list|(
name|kd
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

