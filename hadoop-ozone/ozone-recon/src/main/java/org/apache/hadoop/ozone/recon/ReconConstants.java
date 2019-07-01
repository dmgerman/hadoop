begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.recon
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
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|CONTAINER_DB_SUFFIX
import|;
end_import

begin_comment
comment|/**  * Recon Server constants file.  */
end_comment

begin_class
DECL|class|ReconConstants
specifier|public
specifier|final
class|class
name|ReconConstants
block|{
DECL|method|ReconConstants ()
specifier|private
name|ReconConstants
parameter_list|()
block|{
comment|// Never Constructed
block|}
DECL|field|RECON_CONTAINER_DB
specifier|public
specifier|static
specifier|final
name|String
name|RECON_CONTAINER_DB
init|=
literal|"recon-"
operator|+
name|CONTAINER_DB_SUFFIX
decl_stmt|;
DECL|field|RECON_OM_SNAPSHOT_DB
specifier|public
specifier|static
specifier|final
name|String
name|RECON_OM_SNAPSHOT_DB
init|=
literal|"om.snapshot.db"
decl_stmt|;
DECL|field|CONTAINER_KEY_TABLE
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER_KEY_TABLE
init|=
literal|"containerKeyTable"
decl_stmt|;
DECL|field|FETCH_ALL
specifier|public
specifier|static
specifier|final
name|String
name|FETCH_ALL
init|=
literal|"-1"
decl_stmt|;
DECL|field|RECON_QUERY_PREVKEY
specifier|public
specifier|static
specifier|final
name|String
name|RECON_QUERY_PREVKEY
init|=
literal|"prev-key"
decl_stmt|;
DECL|field|RECON_QUERY_LIMIT
specifier|public
specifier|static
specifier|final
name|String
name|RECON_QUERY_LIMIT
init|=
literal|"limit"
decl_stmt|;
block|}
end_class

end_unit

