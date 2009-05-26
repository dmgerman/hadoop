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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
comment|/**  * Created 21-Jan-2009 13:42:36  */
end_comment

begin_class
DECL|class|TestConfigurationSubclass
specifier|public
class|class
name|TestConfigurationSubclass
extends|extends
name|TestCase
block|{
DECL|field|EMPTY_CONFIGURATION_XML
specifier|private
specifier|static
specifier|final
name|String
name|EMPTY_CONFIGURATION_XML
init|=
literal|"/org/apache/hadoop/conf/empty-configuration.xml"
decl_stmt|;
DECL|method|testGetProps ()
specifier|public
name|void
name|testGetProps
parameter_list|()
block|{
name|SubConf
name|conf
init|=
operator|new
name|SubConf
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Properties
name|properties
init|=
name|conf
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"hadoop.tmp.dir is not set"
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"hadoop.tmp.dir"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testReload ()
specifier|public
name|void
name|testReload
parameter_list|()
throws|throws
name|Throwable
block|{
name|SubConf
name|conf
init|=
operator|new
name|SubConf
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|conf
operator|.
name|isReloaded
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDefaultResource
argument_list|(
name|EMPTY_CONFIGURATION_XML
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|conf
operator|.
name|isReloaded
argument_list|()
argument_list|)
expr_stmt|;
name|Properties
name|properties
init|=
name|conf
operator|.
name|getProperties
argument_list|()
decl_stmt|;
block|}
DECL|method|testReloadNotQuiet ()
specifier|public
name|void
name|testReloadNotQuiet
parameter_list|()
throws|throws
name|Throwable
block|{
name|SubConf
name|conf
init|=
operator|new
name|SubConf
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setQuietMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|conf
operator|.
name|isReloaded
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
literal|"not-a-valid-resource"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|conf
operator|.
name|isReloaded
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Properties
name|properties
init|=
name|conf
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|fail
argument_list|(
literal|"Should not have got here"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"not found"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SubConf
specifier|private
specifier|static
class|class
name|SubConf
extends|extends
name|Configuration
block|{
DECL|field|reloaded
specifier|private
name|boolean
name|reloaded
decl_stmt|;
comment|/**      * A new configuration where the behavior of reading from the default resources      * can be turned off.      *      * If the parameter {@code loadDefaults} is false, the new instance will not      * load resources from the default files.      *      * @param loadDefaults specifies whether to load from the default files      */
DECL|method|SubConf (boolean loadDefaults)
specifier|private
name|SubConf
parameter_list|(
name|boolean
name|loadDefaults
parameter_list|)
block|{
name|super
argument_list|(
name|loadDefaults
argument_list|)
expr_stmt|;
block|}
DECL|method|getProperties ()
specifier|public
name|Properties
name|getProperties
parameter_list|()
block|{
return|return
name|super
operator|.
name|getProps
argument_list|()
return|;
block|}
comment|/**      * {@inheritDoc}.      * Sets the reloaded flag.      */
annotation|@
name|Override
DECL|method|reloadConfiguration ()
specifier|public
name|void
name|reloadConfiguration
parameter_list|()
block|{
name|super
operator|.
name|reloadConfiguration
argument_list|()
expr_stmt|;
name|reloaded
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|isReloaded ()
specifier|public
name|boolean
name|isReloaded
parameter_list|()
block|{
return|return
name|reloaded
return|;
block|}
DECL|method|setReloaded (boolean reloaded)
specifier|public
name|void
name|setReloaded
parameter_list|(
name|boolean
name|reloaded
parameter_list|)
block|{
name|this
operator|.
name|reloaded
operator|=
name|reloaded
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

