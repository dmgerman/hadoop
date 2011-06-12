begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.eclipse.launch
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|eclipse
operator|.
name|launch
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
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
name|eclipse
operator|.
name|servers
operator|.
name|RunOnHadoopWizard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|resources
operator|.
name|IFile
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|resources
operator|.
name|IResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|core
operator|.
name|runtime
operator|.
name|CoreException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|debug
operator|.
name|core
operator|.
name|ILaunchConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|debug
operator|.
name|core
operator|.
name|ILaunchConfigurationType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|debug
operator|.
name|core
operator|.
name|ILaunchConfigurationWorkingCopy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jdt
operator|.
name|core
operator|.
name|IJavaProject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jdt
operator|.
name|core
operator|.
name|IType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jdt
operator|.
name|core
operator|.
name|JavaCore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jdt
operator|.
name|debug
operator|.
name|ui
operator|.
name|launchConfigurations
operator|.
name|JavaApplicationLaunchShortcut
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jdt
operator|.
name|launching
operator|.
name|IJavaLaunchConfigurationConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jdt
operator|.
name|launching
operator|.
name|IRuntimeClasspathEntry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jdt
operator|.
name|launching
operator|.
name|JavaRuntime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jface
operator|.
name|wizard
operator|.
name|IWizard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jface
operator|.
name|wizard
operator|.
name|WizardDialog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|swt
operator|.
name|widgets
operator|.
name|Display
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|swt
operator|.
name|widgets
operator|.
name|Shell
import|;
end_import

begin_comment
comment|/**  * Add a shortcut "Run on Hadoop" to the Run menu  */
end_comment

begin_class
DECL|class|HadoopApplicationLaunchShortcut
specifier|public
class|class
name|HadoopApplicationLaunchShortcut
extends|extends
name|JavaApplicationLaunchShortcut
block|{
DECL|field|log
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|HadoopApplicationLaunchShortcut
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// private ActionDelegate delegate = new RunOnHadoopActionDelegate();
DECL|method|HadoopApplicationLaunchShortcut ()
specifier|public
name|HadoopApplicationLaunchShortcut
parameter_list|()
block|{   }
comment|/* @inheritDoc */
annotation|@
name|Override
DECL|method|findLaunchConfiguration (IType type, ILaunchConfigurationType configType)
specifier|protected
name|ILaunchConfiguration
name|findLaunchConfiguration
parameter_list|(
name|IType
name|type
parameter_list|,
name|ILaunchConfigurationType
name|configType
parameter_list|)
block|{
comment|// Find an existing or create a launch configuration (Standard way)
name|ILaunchConfiguration
name|iConf
init|=
name|super
operator|.
name|findLaunchConfiguration
argument_list|(
name|type
argument_list|,
name|configType
argument_list|)
decl_stmt|;
if|if
condition|(
name|iConf
operator|==
literal|null
condition|)
name|iConf
operator|=
name|super
operator|.
name|createConfiguration
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|ILaunchConfigurationWorkingCopy
name|iConfWC
decl_stmt|;
try|try
block|{
comment|/*        * Tune the default launch configuration: setup run-time classpath        * manually        */
name|iConfWC
operator|=
name|iConf
operator|.
name|getWorkingCopy
argument_list|()
expr_stmt|;
name|iConfWC
operator|.
name|setAttribute
argument_list|(
name|IJavaLaunchConfigurationConstants
operator|.
name|ATTR_DEFAULT_CLASSPATH
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|classPath
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|IResource
name|resource
init|=
name|type
operator|.
name|getResource
argument_list|()
decl_stmt|;
name|IJavaProject
name|project
init|=
operator|(
name|IJavaProject
operator|)
name|resource
operator|.
name|getProject
argument_list|()
operator|.
name|getNature
argument_list|(
name|JavaCore
operator|.
name|NATURE_ID
argument_list|)
decl_stmt|;
name|IRuntimeClasspathEntry
name|cpEntry
init|=
name|JavaRuntime
operator|.
name|newDefaultProjectClasspathEntry
argument_list|(
name|project
argument_list|)
decl_stmt|;
name|classPath
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|cpEntry
operator|.
name|getMemento
argument_list|()
argument_list|)
expr_stmt|;
name|iConfWC
operator|.
name|setAttribute
argument_list|(
name|IJavaLaunchConfigurationConstants
operator|.
name|ATTR_CLASSPATH
argument_list|,
name|classPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CoreException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|// FIXME Error dialog
return|return
literal|null
return|;
block|}
comment|/*      * Update the selected configuration with a specific Hadoop location      * target      */
name|IResource
name|resource
init|=
name|type
operator|.
name|getResource
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|resource
operator|instanceof
name|IFile
operator|)
condition|)
return|return
literal|null
return|;
name|RunOnHadoopWizard
name|wizard
init|=
operator|new
name|RunOnHadoopWizard
argument_list|(
operator|(
name|IFile
operator|)
name|resource
argument_list|,
name|iConfWC
argument_list|)
decl_stmt|;
name|WizardDialog
name|dialog
init|=
operator|new
name|WizardDialog
argument_list|(
name|Display
operator|.
name|getDefault
argument_list|()
operator|.
name|getActiveShell
argument_list|()
argument_list|,
name|wizard
argument_list|)
decl_stmt|;
name|dialog
operator|.
name|create
argument_list|()
expr_stmt|;
name|dialog
operator|.
name|setBlockOnOpen
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|dialog
operator|.
name|open
argument_list|()
operator|!=
name|WizardDialog
operator|.
name|OK
condition|)
return|return
literal|null
return|;
try|try
block|{
comment|// Only save if some configuration is different.
if|if
condition|(
operator|!
name|iConfWC
operator|.
name|contentsEqual
argument_list|(
name|iConf
argument_list|)
condition|)
name|iConfWC
operator|.
name|doSave
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CoreException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
comment|// FIXME Error dialog
return|return
literal|null
return|;
block|}
return|return
name|iConfWC
return|;
block|}
comment|/**    * Was used to run the RunOnHadoopWizard inside and provide it a    * ProgressMonitor    */
DECL|class|Dialog
specifier|static
class|class
name|Dialog
extends|extends
name|WizardDialog
block|{
DECL|method|Dialog (Shell parentShell, IWizard newWizard)
specifier|public
name|Dialog
parameter_list|(
name|Shell
name|parentShell
parameter_list|,
name|IWizard
name|newWizard
parameter_list|)
block|{
name|super
argument_list|(
name|parentShell
argument_list|,
name|newWizard
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create ()
specifier|public
name|void
name|create
parameter_list|()
block|{
name|super
operator|.
name|create
argument_list|()
expr_stmt|;
operator|(
operator|(
name|RunOnHadoopWizard
operator|)
name|getWizard
argument_list|()
operator|)
operator|.
name|setProgressMonitor
argument_list|(
name|getProgressMonitor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

