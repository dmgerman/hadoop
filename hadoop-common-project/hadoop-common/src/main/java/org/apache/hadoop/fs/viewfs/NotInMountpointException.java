begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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
name|fs
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  * NotInMountpointException extends the UnsupportedOperationException.  * Exception class used in cases where the given path is not mounted   * through viewfs.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
comment|/*Evolving for a release,to be changed to Stable */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|NotInMountpointException
specifier|public
class|class
name|NotInMountpointException
extends|extends
name|UnsupportedOperationException
block|{
DECL|field|msg
specifier|final
name|String
name|msg
decl_stmt|;
DECL|method|NotInMountpointException (Path path, String operation)
specifier|public
name|NotInMountpointException
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|operation
parameter_list|)
block|{
name|msg
operator|=
name|operation
operator|+
literal|" on path `"
operator|+
name|path
operator|+
literal|"' is not within a mount point"
expr_stmt|;
block|}
DECL|method|NotInMountpointException (String operation)
specifier|public
name|NotInMountpointException
parameter_list|(
name|String
name|operation
parameter_list|)
block|{
name|msg
operator|=
name|operation
operator|+
literal|" on empty path is invalid"
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|msg
return|;
block|}
block|}
end_class

end_unit

