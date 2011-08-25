begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.eclipse
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|eclipse
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
name|eclipse
operator|.
name|servers
operator|.
name|ServerRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|ui
operator|.
name|plugin
operator|.
name|AbstractUIPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|BundleContext
import|;
end_import

begin_comment
comment|/**  * The activator class controls the plug-in life cycle  */
end_comment

begin_class
DECL|class|Activator
specifier|public
class|class
name|Activator
extends|extends
name|AbstractUIPlugin
block|{
comment|/**    * The plug-in ID    */
DECL|field|PLUGIN_ID
specifier|public
specifier|static
specifier|final
name|String
name|PLUGIN_ID
init|=
literal|"org.apache.hadoop.eclipse"
decl_stmt|;
comment|/**    * The shared unique instance (singleton)    */
DECL|field|plugin
specifier|private
specifier|static
name|Activator
name|plugin
decl_stmt|;
comment|/**    * Constructor    */
DECL|method|Activator ()
specifier|public
name|Activator
parameter_list|()
block|{
synchronized|synchronized
init|(
name|Activator
operator|.
name|class
init|)
block|{
if|if
condition|(
name|plugin
operator|!=
literal|null
condition|)
block|{
comment|// Not a singleton!?
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Activator for "
operator|+
name|PLUGIN_ID
operator|+
literal|" is not a singleton"
argument_list|)
throw|;
block|}
name|plugin
operator|=
name|this
expr_stmt|;
block|}
block|}
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|start (BundleContext context)
specifier|public
name|void
name|start
parameter_list|(
name|BundleContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|start
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|stop (BundleContext context)
specifier|public
name|void
name|stop
parameter_list|(
name|BundleContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|ServerRegistry
operator|.
name|getInstance
argument_list|()
operator|.
name|dispose
argument_list|()
expr_stmt|;
name|plugin
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|stop
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the shared unique instance (singleton)    *     * @return the shared unique instance (singleton)    */
DECL|method|getDefault ()
specifier|public
specifier|static
name|Activator
name|getDefault
parameter_list|()
block|{
return|return
name|plugin
return|;
block|}
block|}
end_class

end_unit

