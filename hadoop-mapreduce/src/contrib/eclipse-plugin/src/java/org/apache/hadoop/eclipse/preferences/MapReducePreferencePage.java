begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.eclipse.preferences
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|eclipse
operator|.
name|preferences
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
name|Activator
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
name|preference
operator|.
name|DirectoryFieldEditor
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
name|preference
operator|.
name|FieldEditorPreferencePage
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
name|IWorkbench
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
name|IWorkbenchPreferencePage
import|;
end_import

begin_comment
comment|/**  * This class represents a preference page that is contributed to the  * Preferences dialog. By sub-classing<tt>FieldEditorPreferencePage</tt>,  * we can use the field support built into JFace that allows us to create a  * page that is small and knows how to save, restore and apply itself.  *   *<p>  * This page is used to modify preferences only. They are stored in the  * preference store that belongs to the main plug-in class. That way,  * preferences can be accessed directly via the preference store.  */
end_comment

begin_class
DECL|class|MapReducePreferencePage
specifier|public
class|class
name|MapReducePreferencePage
extends|extends
name|FieldEditorPreferencePage
implements|implements
name|IWorkbenchPreferencePage
block|{
DECL|method|MapReducePreferencePage ()
specifier|public
name|MapReducePreferencePage
parameter_list|()
block|{
name|super
argument_list|(
name|GRID
argument_list|)
expr_stmt|;
name|setPreferenceStore
argument_list|(
name|Activator
operator|.
name|getDefault
argument_list|()
operator|.
name|getPreferenceStore
argument_list|()
argument_list|)
expr_stmt|;
name|setTitle
argument_list|(
literal|"Hadoop Map/Reduce Tools"
argument_list|)
expr_stmt|;
comment|// setDescription("Hadoop Map/Reduce Preferences");
block|}
comment|/**    * Creates the field editors. Field editors are abstractions of the common    * GUI blocks needed to manipulate various types of preferences. Each field    * editor knows how to save and restore itself.    */
annotation|@
name|Override
DECL|method|createFieldEditors ()
specifier|public
name|void
name|createFieldEditors
parameter_list|()
block|{
name|addField
argument_list|(
operator|new
name|DirectoryFieldEditor
argument_list|(
name|PreferenceConstants
operator|.
name|P_PATH
argument_list|,
literal|"&Hadoop installation directory:"
argument_list|,
name|getFieldEditorParent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* @inheritDoc */
DECL|method|init (IWorkbench workbench)
specifier|public
name|void
name|init
parameter_list|(
name|IWorkbench
name|workbench
parameter_list|)
block|{   }
block|}
end_class

end_unit

