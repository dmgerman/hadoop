begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|impl
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
name|configuration2
operator|.
name|PropertiesConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|configuration2
operator|.
name|SubsetConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|configuration2
operator|.
name|convert
operator|.
name|DefaultListDelimiterHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_comment
comment|/**  * Helper class for building configs, mostly used in tests  */
end_comment

begin_class
DECL|class|ConfigBuilder
specifier|public
class|class
name|ConfigBuilder
block|{
comment|/** The built config */
DECL|field|config
specifier|public
specifier|final
name|PropertiesConfiguration
name|config
decl_stmt|;
comment|/**    * Default constructor    */
DECL|method|ConfigBuilder ()
specifier|public
name|ConfigBuilder
parameter_list|()
block|{
name|config
operator|=
operator|new
name|PropertiesConfiguration
argument_list|()
expr_stmt|;
name|config
operator|.
name|setListDelimiterHandler
argument_list|(
operator|new
name|DefaultListDelimiterHandler
argument_list|(
literal|','
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a property to the config    * @param key of the property    * @param value of the property    * @return self    */
DECL|method|add (String key, Object value)
specifier|public
name|ConfigBuilder
name|add
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|config
operator|.
name|addProperty
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Save the config to a file    * @param filename  to save    * @return self    * @throws RuntimeException    */
DECL|method|save (String filename)
specifier|public
name|ConfigBuilder
name|save
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
try|try
block|{
name|FileWriter
name|fw
init|=
operator|new
name|FileWriter
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|config
operator|.
name|write
argument_list|(
name|fw
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error saving config"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
comment|/**    * Return a subset configuration (so getParent() can be used.)    * @param prefix  of the subset    * @return the subset config    */
DECL|method|subset (String prefix)
specifier|public
name|SubsetConfiguration
name|subset
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
operator|new
name|SubsetConfiguration
argument_list|(
name|config
argument_list|,
name|prefix
argument_list|,
literal|"."
argument_list|)
return|;
block|}
block|}
end_class

end_unit

