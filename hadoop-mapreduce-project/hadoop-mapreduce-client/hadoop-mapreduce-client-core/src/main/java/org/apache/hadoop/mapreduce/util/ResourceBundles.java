begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ResourceBundle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|MissingResourceException
import|;
end_import

begin_comment
comment|/**  * Helper class to handle resource bundles in a saner way  */
end_comment

begin_class
DECL|class|ResourceBundles
specifier|public
class|class
name|ResourceBundles
block|{
comment|/**    * Get a resource bundle    * @param bundleName of the resource    * @return the resource bundle    * @throws MissingResourceException    */
DECL|method|getBundle (String bundleName)
specifier|public
specifier|static
name|ResourceBundle
name|getBundle
parameter_list|(
name|String
name|bundleName
parameter_list|)
block|{
return|return
name|ResourceBundle
operator|.
name|getBundle
argument_list|(
name|bundleName
operator|.
name|replace
argument_list|(
literal|'$'
argument_list|,
literal|'_'
argument_list|)
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get a resource given bundle name and key    * @param<T> type of the resource    * @param bundleName name of the resource bundle    * @param key to lookup the resource    * @param suffix for the key to lookup    * @param defaultValue of the resource    * @return the resource or the defaultValue    * @throws ClassCastException if the resource found doesn't match T    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|getValue (String bundleName, String key, String suffix, T defaultValue)
specifier|public
specifier|static
specifier|synchronized
parameter_list|<
name|T
parameter_list|>
name|T
name|getValue
parameter_list|(
name|String
name|bundleName
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|suffix
parameter_list|,
name|T
name|defaultValue
parameter_list|)
block|{
name|T
name|value
decl_stmt|;
try|try
block|{
name|ResourceBundle
name|bundle
init|=
name|getBundle
argument_list|(
name|bundleName
argument_list|)
decl_stmt|;
name|value
operator|=
operator|(
name|T
operator|)
name|bundle
operator|.
name|getObject
argument_list|(
name|getLookupKey
argument_list|(
name|key
argument_list|,
name|suffix
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|value
return|;
block|}
DECL|method|getLookupKey (String key, String suffix)
specifier|private
specifier|static
name|String
name|getLookupKey
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
if|if
condition|(
name|suffix
operator|==
literal|null
operator|||
name|suffix
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|key
return|;
return|return
name|key
operator|+
name|suffix
return|;
block|}
comment|/**    * Get the counter group display name    * @param group the group name to lookup    * @param defaultValue of the group    * @return the group display name    */
DECL|method|getCounterGroupName (String group, String defaultValue)
specifier|public
specifier|static
name|String
name|getCounterGroupName
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
return|return
name|getValue
argument_list|(
name|group
argument_list|,
literal|"CounterGroupName"
argument_list|,
literal|""
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
comment|/**    * Get the counter display name    * @param group the counter group name for the counter    * @param counter the counter name to lookup    * @param defaultValue of the counter    * @return the counter display name    */
DECL|method|getCounterName (String group, String counter, String defaultValue)
specifier|public
specifier|static
name|String
name|getCounterName
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|counter
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
return|return
name|getValue
argument_list|(
name|group
argument_list|,
name|counter
argument_list|,
literal|".name"
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
block|}
end_class

end_unit

