begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.hadoop.ozone.recon.codegen
package|package
name|org
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|codegen
package|;
end_package

begin_import
import|import
name|org
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|schema
operator|.
name|ReconSchemaDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|schema
operator|.
name|UtilizationSchemaDefinition
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|AbstractModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|multibindings
operator|.
name|Multibinder
import|;
end_import

begin_comment
comment|/**  * Bindings for DDL generation and used by  * {@link org.hadoop.ozone.recon.codegen.JooqCodeGenerator}.  */
end_comment

begin_class
DECL|class|ReconSchemaGenerationModule
specifier|public
class|class
name|ReconSchemaGenerationModule
extends|extends
name|AbstractModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
comment|// SQL schema creation and related bindings
name|Multibinder
argument_list|<
name|ReconSchemaDefinition
argument_list|>
name|schemaBinder
init|=
name|Multibinder
operator|.
name|newSetBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|ReconSchemaDefinition
operator|.
name|class
argument_list|)
decl_stmt|;
name|schemaBinder
operator|.
name|addBinding
argument_list|()
operator|.
name|to
argument_list|(
name|UtilizationSchemaDefinition
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

