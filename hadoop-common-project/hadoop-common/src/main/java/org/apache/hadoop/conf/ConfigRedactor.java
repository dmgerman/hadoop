begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
package|;
end_package

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
name|Arrays
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeys
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Tool for redacting sensitive information when displaying config parameters.  *  *<p>Some config parameters contain sensitive information (for example, cloud  * storage keys). When these properties are displayed in plaintext, we should  * redactor their values as appropriate.  */
end_comment

begin_class
DECL|class|ConfigRedactor
specifier|public
class|class
name|ConfigRedactor
block|{
DECL|field|REDACTED_TEXT
specifier|private
specifier|static
specifier|final
name|String
name|REDACTED_TEXT
init|=
literal|"<redacted>"
decl_stmt|;
DECL|field|compiledPatterns
specifier|private
name|List
argument_list|<
name|Pattern
argument_list|>
name|compiledPatterns
decl_stmt|;
DECL|method|ConfigRedactor (Configuration conf)
specifier|public
name|ConfigRedactor
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|sensitiveRegexList
init|=
name|conf
operator|.
name|get
argument_list|(
name|HADOOP_SECURITY_SENSITIVE_CONFIG_KEYS
argument_list|,
name|HADOOP_SECURITY_SENSITIVE_CONFIG_KEYS_DEFAULT
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|sensitiveRegexes
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|sensitiveRegexList
operator|.
name|split
argument_list|(
literal|","
argument_list|)
argument_list|)
decl_stmt|;
name|compiledPatterns
operator|=
operator|new
name|ArrayList
argument_list|<
name|Pattern
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|regex
range|:
name|sensitiveRegexes
control|)
block|{
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
argument_list|)
decl_stmt|;
name|compiledPatterns
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Given a key / value pair, decides whether or not to redact and returns    * either the original value or text indicating it has been redacted.    *    * @param key    * @param value    * @return Original value, or text indicating it has been redacted    */
DECL|method|redact (String key, String value)
specifier|public
name|String
name|redact
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|configIsSensitive
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|REDACTED_TEXT
return|;
block|}
return|return
name|value
return|;
block|}
comment|/**    * Matches given config key against patterns and determines whether or not    * it should be considered sensitive enough to redact in logs and other    * plaintext displays.    *    * @param key    * @return True if parameter is considered sensitive    */
DECL|method|configIsSensitive (String key)
specifier|private
name|boolean
name|configIsSensitive
parameter_list|(
name|String
name|key
parameter_list|)
block|{
for|for
control|(
name|Pattern
name|regex
range|:
name|compiledPatterns
control|)
block|{
if|if
condition|(
name|regex
operator|.
name|matcher
argument_list|(
name|key
argument_list|)
operator|.
name|find
argument_list|()
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

