begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

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
name|conf
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/*  * A simple interface for a client MR cluster used for testing. This interface  * provides basic methods which are independent of the underlying Mini Cluster (  * either through MR1 or MR2).  */
end_comment

begin_interface
DECL|interface|MiniMRClientCluster
specifier|public
interface|interface
name|MiniMRClientCluster
block|{
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Stop and start back the cluster using the same configuration.    */
DECL|method|restart ()
specifier|public
name|void
name|restart
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getConfig ()
specifier|public
name|Configuration
name|getConfig
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

