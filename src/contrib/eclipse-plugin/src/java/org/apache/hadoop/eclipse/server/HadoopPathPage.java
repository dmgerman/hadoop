begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.eclipse.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|eclipse
operator|.
name|server
package|;
end_package

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
name|IProgressMonitor
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
name|graphics
operator|.
name|Image
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
name|Composite
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
name|IEditorInput
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
name|IEditorPart
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
name|IEditorSite
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
name|IPropertyListener
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
name|IWorkbenchPartSite
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
name|PartInitException
import|;
end_import

begin_class
DECL|class|HadoopPathPage
specifier|public
class|class
name|HadoopPathPage
implements|implements
name|IEditorPart
block|{
DECL|method|getEditorInput ()
specifier|public
name|IEditorInput
name|getEditorInput
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
DECL|method|getEditorSite ()
specifier|public
name|IEditorSite
name|getEditorSite
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
DECL|method|init (IEditorSite site, IEditorInput input)
specifier|public
name|void
name|init
parameter_list|(
name|IEditorSite
name|site
parameter_list|,
name|IEditorInput
name|input
parameter_list|)
throws|throws
name|PartInitException
block|{
comment|// TODO Auto-generated method stub
block|}
DECL|method|addPropertyListener (IPropertyListener listener)
specifier|public
name|void
name|addPropertyListener
parameter_list|(
name|IPropertyListener
name|listener
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
DECL|method|createPartControl (Composite parent)
specifier|public
name|void
name|createPartControl
parameter_list|(
name|Composite
name|parent
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
DECL|method|dispose ()
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
DECL|method|getSite ()
specifier|public
name|IWorkbenchPartSite
name|getSite
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
DECL|method|getTitle ()
specifier|public
name|String
name|getTitle
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
DECL|method|getTitleImage ()
specifier|public
name|Image
name|getTitleImage
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
DECL|method|getTitleToolTip ()
specifier|public
name|String
name|getTitleToolTip
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
DECL|method|removePropertyListener (IPropertyListener listener)
specifier|public
name|void
name|removePropertyListener
parameter_list|(
name|IPropertyListener
name|listener
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
DECL|method|setFocus ()
specifier|public
name|void
name|setFocus
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
DECL|method|getAdapter (Class adapter)
specifier|public
name|Object
name|getAdapter
parameter_list|(
name|Class
name|adapter
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
DECL|method|doSave (IProgressMonitor monitor)
specifier|public
name|void
name|doSave
parameter_list|(
name|IProgressMonitor
name|monitor
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
DECL|method|doSaveAs ()
specifier|public
name|void
name|doSaveAs
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
DECL|method|isDirty ()
specifier|public
name|boolean
name|isDirty
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
DECL|method|isSaveAsAllowed ()
specifier|public
name|boolean
name|isSaveAsAllowed
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
DECL|method|isSaveOnCloseNeeded ()
specifier|public
name|boolean
name|isSaveOnCloseNeeded
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

