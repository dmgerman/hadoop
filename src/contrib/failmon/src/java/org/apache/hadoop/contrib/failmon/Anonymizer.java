begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.failmon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|failmon
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_comment
comment|/**********************************************************  * This class provides anonymization to SerializedRecord objects. It   * anonymizes all hostnames, ip addresses and file names/paths  * that appear in EventRecords gathered from the logs  * and other system utilities. Such values are hashed using a  * cryptographically safe one-way-hash algorithm (MD5).  *   **********************************************************/
end_comment

begin_class
DECL|class|Anonymizer
specifier|public
class|class
name|Anonymizer
block|{
comment|/** 	 * Anonymize hostnames, ip addresses and file names/paths    * that appear in fields of a SerializedRecord.    *  	 * @param sr the input SerializedRecord 	 *  	 * @return the anonymized SerializedRecord 	 */
DECL|method|anonymize (SerializedRecord sr)
specifier|public
specifier|static
name|SerializedRecord
name|anonymize
parameter_list|(
name|SerializedRecord
name|sr
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|hostname
init|=
name|sr
operator|.
name|get
argument_list|(
literal|"hostname"
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostname
operator|==
literal|null
condition|)
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Malformed SerializedRecord: no hostname found"
argument_list|)
throw|;
if|if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|Environment
operator|.
name|getProperty
argument_list|(
literal|"anonymizer.hash.hostnames"
argument_list|)
argument_list|)
condition|)
block|{
comment|// hash the node's hostname
name|anonymizeField
argument_list|(
name|sr
argument_list|,
literal|"message"
argument_list|,
name|hostname
argument_list|,
literal|"_hn_"
argument_list|)
expr_stmt|;
name|anonymizeField
argument_list|(
name|sr
argument_list|,
literal|"hostname"
argument_list|,
name|hostname
argument_list|,
literal|"_hn_"
argument_list|)
expr_stmt|;
comment|// hash all other hostnames
name|String
name|suffix
init|=
name|Environment
operator|.
name|getProperty
argument_list|(
literal|"anonymizer.hostname.suffix"
argument_list|)
decl_stmt|;
if|if
condition|(
name|suffix
operator|!=
literal|null
condition|)
name|anonymizeField
argument_list|(
name|sr
argument_list|,
literal|"message"
argument_list|,
literal|"(\\S+\\.)*"
operator|+
name|suffix
argument_list|,
literal|"_hn_"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|Environment
operator|.
name|getProperty
argument_list|(
literal|"anonymizer.hash.ips"
argument_list|)
argument_list|)
condition|)
block|{
comment|// hash all ip addresses
name|String
name|ipPattern
init|=
literal|"(\\d{1,3}\\.){3}\\d{1,3}"
decl_stmt|;
name|anonymizeField
argument_list|(
name|sr
argument_list|,
literal|"message"
argument_list|,
name|ipPattern
argument_list|,
literal|"_ip_"
argument_list|)
expr_stmt|;
name|anonymizeField
argument_list|(
name|sr
argument_list|,
literal|"ips"
argument_list|,
name|ipPattern
argument_list|,
literal|"_ip_"
argument_list|)
expr_stmt|;
comment|// if multiple ips are present for a node:
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|sr
operator|.
name|get
argument_list|(
literal|"ips"
operator|+
literal|"#"
operator|+
name|i
argument_list|)
operator|!=
literal|null
condition|)
name|anonymizeField
argument_list|(
name|sr
argument_list|,
literal|"ips"
operator|+
literal|"#"
operator|+
name|i
operator|++
argument_list|,
name|ipPattern
argument_list|,
literal|"_ip_"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"NIC"
operator|.
name|equalsIgnoreCase
argument_list|(
name|sr
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|)
condition|)
name|anonymizeField
argument_list|(
name|sr
argument_list|,
literal|"ipAddress"
argument_list|,
name|ipPattern
argument_list|,
literal|"_ip_"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|Environment
operator|.
name|getProperty
argument_list|(
literal|"anonymizer.hash.filenames"
argument_list|)
argument_list|)
condition|)
block|{
comment|// hash every filename present in messages
name|anonymizeField
argument_list|(
name|sr
argument_list|,
literal|"message"
argument_list|,
literal|"\\s+/(\\S+/)*[^:\\s]*"
argument_list|,
literal|" _fn_"
argument_list|)
expr_stmt|;
name|anonymizeField
argument_list|(
name|sr
argument_list|,
literal|"message"
argument_list|,
literal|"\\s+hdfs://(\\S+/)*[^:\\s]*"
argument_list|,
literal|" hdfs://_fn_"
argument_list|)
expr_stmt|;
block|}
return|return
name|sr
return|;
block|}
comment|/**    * Anonymize hostnames, ip addresses and file names/paths    * that appear in fields of an EventRecord, after it gets    * serialized into a SerializedRecord.    *     * @param er the input EventRecord    *     * @return the anonymized SerializedRecord    */
DECL|method|anonymize (EventRecord er)
specifier|public
specifier|static
name|SerializedRecord
name|anonymize
parameter_list|(
name|EventRecord
name|er
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|anonymize
argument_list|(
operator|new
name|SerializedRecord
argument_list|(
name|er
argument_list|)
argument_list|)
return|;
block|}
DECL|method|anonymizeField (SerializedRecord sr, String fieldName, String pattern, String prefix)
specifier|private
specifier|static
name|String
name|anonymizeField
parameter_list|(
name|SerializedRecord
name|sr
parameter_list|,
name|String
name|fieldName
parameter_list|,
name|String
name|pattern
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|String
name|txt
init|=
name|sr
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|txt
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
block|{
name|String
name|anon
init|=
name|getMD5Hash
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
name|sr
operator|.
name|set
argument_list|(
name|fieldName
argument_list|,
name|txt
operator|.
name|replaceAll
argument_list|(
name|pattern
argument_list|,
operator|(
name|prefix
operator|==
literal|null
condition|?
literal|""
else|:
name|prefix
operator|)
operator|+
name|anon
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|txt
return|;
block|}
block|}
comment|/**    * Create the MD5 digest of an input text.    *     * @param text the input text    *     * @return the hexadecimal representation of the MD5 digest    */
DECL|method|getMD5Hash (String text)
specifier|public
specifier|static
name|String
name|getMD5Hash
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|MessageDigest
name|md
decl_stmt|;
name|byte
index|[]
name|md5hash
init|=
operator|new
name|byte
index|[
literal|32
index|]
decl_stmt|;
try|try
block|{
name|md
operator|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
expr_stmt|;
name|md
operator|.
name|update
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
literal|"iso-8859-1"
argument_list|)
argument_list|,
literal|0
argument_list|,
name|text
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|md5hash
operator|=
name|md
operator|.
name|digest
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
name|convertToHex
argument_list|(
name|md5hash
argument_list|)
return|;
block|}
DECL|method|convertToHex (byte[] data)
specifier|private
specifier|static
name|String
name|convertToHex
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|halfbyte
init|=
operator|(
name|data
index|[
name|i
index|]
operator|>>>
literal|4
operator|)
operator|&
literal|0x0F
decl_stmt|;
name|int
name|two_halfs
init|=
literal|0
decl_stmt|;
do|do
block|{
if|if
condition|(
operator|(
literal|0
operator|<=
name|halfbyte
operator|)
operator|&&
operator|(
name|halfbyte
operator|<=
literal|9
operator|)
condition|)
name|buf
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|'0'
operator|+
name|halfbyte
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|buf
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
literal|'a'
operator|+
operator|(
name|halfbyte
operator|-
literal|10
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|halfbyte
operator|=
name|data
index|[
name|i
index|]
operator|&
literal|0x0F
expr_stmt|;
block|}
do|while
condition|(
name|two_halfs
operator|++
operator|<
literal|1
condition|)
do|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

