begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon.recovery
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|recon
operator|.
name|recovery
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|ozone
operator|.
name|om
operator|.
name|OMMetadataManager
import|;
end_import

begin_comment
comment|/**  * Interface for the OM Metadata Manager + DB store maintained by  * Recon.  */
end_comment

begin_interface
DECL|interface|ReconOMMetadataManager
specifier|public
interface|interface
name|ReconOMMetadataManager
extends|extends
name|OMMetadataManager
block|{
comment|/**    * Refresh the DB instance to point to a new location. Get rid of the old    * DB instance.    * @param dbLocation New location of the OM Snapshot DB.    */
DECL|method|updateOmDB (File dbLocation)
name|void
name|updateOmDB
parameter_list|(
name|File
name|dbLocation
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

