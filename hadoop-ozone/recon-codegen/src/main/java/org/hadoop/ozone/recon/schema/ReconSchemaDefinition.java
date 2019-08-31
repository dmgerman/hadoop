begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.hadoop.ozone.recon.schema
package|package
name|org
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|schema
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_comment
comment|/**  * Classes meant to initialize the SQL schema for Recon. The implementations of  * this class will be used to create the SQL schema programmatically.  * Note: Make sure add a binding for your implementation to the Guice module,  * otherwise code-generator will not pick up the schema changes.  */
end_comment

begin_interface
DECL|interface|ReconSchemaDefinition
specifier|public
interface|interface
name|ReconSchemaDefinition
block|{
comment|/**    * Execute DDL that will create Recon schema.    */
DECL|method|initializeSchema ()
name|void
name|initializeSchema
parameter_list|()
throws|throws
name|SQLException
function_decl|;
block|}
end_interface

end_unit

