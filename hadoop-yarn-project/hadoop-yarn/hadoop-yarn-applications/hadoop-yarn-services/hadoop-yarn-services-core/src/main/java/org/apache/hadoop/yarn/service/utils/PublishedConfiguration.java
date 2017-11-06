begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|utils
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
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ConfigFormat
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
name|yarn
operator|.
name|service
operator|.
name|exceptions
operator|.
name|BadConfigException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|annotate
operator|.
name|JsonIgnoreProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|SerializationConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|annotate
operator|.
name|JsonSerialize
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * JSON-serializable description of a published key-val configuration.  *   * The values themselves are not serialized in the external view; they have  * to be served up by the far end  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
annotation|@
name|JsonSerialize
argument_list|(
name|include
operator|=
name|JsonSerialize
operator|.
name|Inclusion
operator|.
name|NON_NULL
argument_list|)
DECL|class|PublishedConfiguration
specifier|public
class|class
name|PublishedConfiguration
block|{
DECL|field|description
specifier|public
name|String
name|description
decl_stmt|;
DECL|field|updated
specifier|public
name|long
name|updated
decl_stmt|;
DECL|field|updatedTime
specifier|public
name|String
name|updatedTime
decl_stmt|;
DECL|field|entries
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entries
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|PublishedConfiguration ()
specifier|public
name|PublishedConfiguration
parameter_list|()
block|{   }
comment|/**    * build an empty published configuration     * @param description configuration description    */
DECL|method|PublishedConfiguration (String description)
specifier|public
name|PublishedConfiguration
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
comment|/**    * Build a configuration from the entries    * @param description configuration description    * @param entries entries to put    */
DECL|method|PublishedConfiguration (String description, Iterable<Map.Entry<String, String>> entries)
specifier|public
name|PublishedConfiguration
parameter_list|(
name|String
name|description
parameter_list|,
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entries
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|putValues
argument_list|(
name|entries
argument_list|)
expr_stmt|;
block|}
comment|/**    * Build a published configuration, using the keys from keysource,    * but resolving the values from the value source, via Configuration.get()    * @param description configuration description    * @param keysource source of keys    * @param valuesource source of values    */
DECL|method|PublishedConfiguration (String description, Iterable<Map.Entry<String, String>> keysource, Configuration valuesource)
specifier|public
name|PublishedConfiguration
parameter_list|(
name|String
name|description
parameter_list|,
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|keysource
parameter_list|,
name|Configuration
name|valuesource
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
name|putValues
argument_list|(
name|ConfigHelper
operator|.
name|resolveConfiguration
argument_list|(
name|keysource
argument_list|,
name|valuesource
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Is the configuration empty. This means either that it has not    * been given any values, or it is stripped down copy set down over the    * wire.    * @return true if it is empty    */
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|entries
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|setUpdated (long updated)
specifier|public
name|void
name|setUpdated
parameter_list|(
name|long
name|updated
parameter_list|)
block|{
name|this
operator|.
name|updated
operator|=
name|updated
expr_stmt|;
name|this
operator|.
name|updatedTime
operator|=
operator|new
name|Date
argument_list|(
name|updated
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|getUpdated ()
specifier|public
name|long
name|getUpdated
parameter_list|()
block|{
return|return
name|updated
return|;
block|}
comment|/**    * Set the values from an iterable (this includes a Hadoop Configuration    * and Java properties object).    * Any existing value set is discarded    * @param entries entries to put    */
DECL|method|putValues (Iterable<Map.Entry<String, String>> entries)
specifier|public
name|void
name|putValues
parameter_list|(
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|entries
parameter_list|)
block|{
name|this
operator|.
name|entries
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|entries
control|)
block|{
name|this
operator|.
name|entries
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Convert to Hadoop XML    * @return the configuration as a Hadoop Configuratin    */
DECL|method|asConfiguration ()
specifier|public
name|Configuration
name|asConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|ConfigHelper
operator|.
name|addConfigMap
argument_list|(
name|conf
argument_list|,
name|entries
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadConfigException
name|e
parameter_list|)
block|{
comment|// triggered on a null value; switch to a runtime (and discard the stack)
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|conf
return|;
block|}
DECL|method|asConfigurationXML ()
specifier|public
name|String
name|asConfigurationXML
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|ConfigHelper
operator|.
name|toXml
argument_list|(
name|asConfiguration
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Convert values to properties    * @return a property file    */
DECL|method|asProperties ()
specifier|public
name|Properties
name|asProperties
parameter_list|()
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|putAll
argument_list|(
name|entries
argument_list|)
expr_stmt|;
return|return
name|props
return|;
block|}
comment|/**    * Return the values as json string    * @return the JSON representation    * @throws IOException marshalling failure    */
DECL|method|asJson ()
specifier|public
name|String
name|asJson
parameter_list|()
throws|throws
name|IOException
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|SerializationConfig
operator|.
name|Feature
operator|.
name|INDENT_OUTPUT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|json
init|=
name|mapper
operator|.
name|writeValueAsString
argument_list|(
name|entries
argument_list|)
decl_stmt|;
return|return
name|json
return|;
block|}
comment|/**    * This makes a copy without the nested content -so is suitable    * for returning as part of the list of a parent's values    * @return the copy    */
DECL|method|shallowCopy ()
specifier|public
name|PublishedConfiguration
name|shallowCopy
parameter_list|()
block|{
name|PublishedConfiguration
name|that
init|=
operator|new
name|PublishedConfiguration
argument_list|()
decl_stmt|;
name|that
operator|.
name|description
operator|=
name|this
operator|.
name|description
expr_stmt|;
name|that
operator|.
name|updated
operator|=
name|this
operator|.
name|updated
expr_stmt|;
name|that
operator|.
name|updatedTime
operator|=
name|this
operator|.
name|updatedTime
expr_stmt|;
return|return
name|that
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"PublishedConfiguration{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"description='"
argument_list|)
operator|.
name|append
argument_list|(
name|description
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" entries = "
argument_list|)
operator|.
name|append
argument_list|(
name|entries
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Create an outputter for a given format    * @param format format to use    * @return an instance of output    */
DECL|method|createOutputter (ConfigFormat format)
specifier|public
name|PublishedConfigurationOutputter
name|createOutputter
parameter_list|(
name|ConfigFormat
name|format
parameter_list|)
block|{
return|return
name|PublishedConfigurationOutputter
operator|.
name|createOutputter
argument_list|(
name|format
argument_list|,
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

