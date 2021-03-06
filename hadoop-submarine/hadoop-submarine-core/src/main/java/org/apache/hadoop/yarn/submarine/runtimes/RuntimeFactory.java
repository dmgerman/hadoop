begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.runtimes
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|runtimes
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|yarn
operator|.
name|submarine
operator|.
name|common
operator|.
name|ClientContext
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
name|submarine
operator|.
name|common
operator|.
name|conf
operator|.
name|SubmarineConfiguration
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
name|submarine
operator|.
name|common
operator|.
name|exception
operator|.
name|SubmarineRuntimeException
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
name|submarine
operator|.
name|runtimes
operator|.
name|common
operator|.
name|JobMonitor
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
name|submarine
operator|.
name|runtimes
operator|.
name|common
operator|.
name|JobSubmitter
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
name|submarine
operator|.
name|runtimes
operator|.
name|common
operator|.
name|SubmarineStorage
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_class
DECL|class|RuntimeFactory
specifier|public
specifier|abstract
class|class
name|RuntimeFactory
block|{
DECL|field|clientContext
specifier|protected
name|ClientContext
name|clientContext
decl_stmt|;
DECL|field|jobSubmitter
specifier|private
name|JobSubmitter
name|jobSubmitter
decl_stmt|;
DECL|field|jobMonitor
specifier|private
name|JobMonitor
name|jobMonitor
decl_stmt|;
DECL|field|submarineStorage
specifier|private
name|SubmarineStorage
name|submarineStorage
decl_stmt|;
DECL|method|RuntimeFactory (ClientContext clientContext)
specifier|public
name|RuntimeFactory
parameter_list|(
name|ClientContext
name|clientContext
parameter_list|)
block|{
name|this
operator|.
name|clientContext
operator|=
name|clientContext
expr_stmt|;
block|}
DECL|method|getRuntimeFactory ( ClientContext clientContext)
specifier|public
specifier|static
name|RuntimeFactory
name|getRuntimeFactory
parameter_list|(
name|ClientContext
name|clientContext
parameter_list|)
block|{
name|Configuration
name|submarineConfiguration
init|=
name|clientContext
operator|.
name|getSubmarineConfig
argument_list|()
decl_stmt|;
name|String
name|runtimeClass
init|=
name|submarineConfiguration
operator|.
name|get
argument_list|(
name|SubmarineConfiguration
operator|.
name|RUNTIME_CLASS
argument_list|,
name|SubmarineConfiguration
operator|.
name|DEFAULT_RUNTIME_CLASS
argument_list|)
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|runtimeClazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|runtimeClass
argument_list|)
decl_stmt|;
if|if
condition|(
name|RuntimeFactory
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|runtimeClazz
argument_list|)
condition|)
block|{
return|return
operator|(
name|RuntimeFactory
operator|)
name|runtimeClazz
operator|.
name|getConstructor
argument_list|(
name|ClientContext
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|clientContext
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|SubmarineRuntimeException
argument_list|(
literal|"Class: "
operator|+
name|runtimeClass
operator|+
literal|" not instance of "
operator|+
name|RuntimeFactory
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
decl||
name|IllegalAccessException
decl||
name|InstantiationException
decl||
name|NoSuchMethodException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SubmarineRuntimeException
argument_list|(
literal|"Could not instantiate RuntimeFactory: "
operator|+
name|runtimeClass
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|internalCreateJobSubmitter ()
specifier|protected
specifier|abstract
name|JobSubmitter
name|internalCreateJobSubmitter
parameter_list|()
function_decl|;
DECL|method|internalCreateJobMonitor ()
specifier|protected
specifier|abstract
name|JobMonitor
name|internalCreateJobMonitor
parameter_list|()
function_decl|;
DECL|method|internalCreateSubmarineStorage ()
specifier|protected
specifier|abstract
name|SubmarineStorage
name|internalCreateSubmarineStorage
parameter_list|()
function_decl|;
DECL|method|getJobSubmitterInstance ()
specifier|public
specifier|synchronized
name|JobSubmitter
name|getJobSubmitterInstance
parameter_list|()
block|{
if|if
condition|(
name|jobSubmitter
operator|==
literal|null
condition|)
block|{
name|jobSubmitter
operator|=
name|internalCreateJobSubmitter
argument_list|()
expr_stmt|;
block|}
return|return
name|jobSubmitter
return|;
block|}
DECL|method|getJobMonitorInstance ()
specifier|public
specifier|synchronized
name|JobMonitor
name|getJobMonitorInstance
parameter_list|()
block|{
if|if
condition|(
name|jobMonitor
operator|==
literal|null
condition|)
block|{
name|jobMonitor
operator|=
name|internalCreateJobMonitor
argument_list|()
expr_stmt|;
block|}
return|return
name|jobMonitor
return|;
block|}
DECL|method|getSubmarineStorage ()
specifier|public
specifier|synchronized
name|SubmarineStorage
name|getSubmarineStorage
parameter_list|()
block|{
if|if
condition|(
name|submarineStorage
operator|==
literal|null
condition|)
block|{
name|submarineStorage
operator|=
name|internalCreateSubmarineStorage
argument_list|()
expr_stmt|;
block|}
return|return
name|submarineStorage
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setJobSubmitterInstance (JobSubmitter jobSubmitter)
specifier|public
specifier|synchronized
name|void
name|setJobSubmitterInstance
parameter_list|(
name|JobSubmitter
name|jobSubmitter
parameter_list|)
block|{
name|this
operator|.
name|jobSubmitter
operator|=
name|jobSubmitter
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setJobMonitorInstance (JobMonitor jobMonitor)
specifier|public
specifier|synchronized
name|void
name|setJobMonitorInstance
parameter_list|(
name|JobMonitor
name|jobMonitor
parameter_list|)
block|{
name|this
operator|.
name|jobMonitor
operator|=
name|jobMonitor
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setSubmarineStorage (SubmarineStorage storage)
specifier|public
specifier|synchronized
name|void
name|setSubmarineStorage
parameter_list|(
name|SubmarineStorage
name|storage
parameter_list|)
block|{
name|this
operator|.
name|submarineStorage
operator|=
name|storage
expr_stmt|;
block|}
block|}
end_class

end_unit

