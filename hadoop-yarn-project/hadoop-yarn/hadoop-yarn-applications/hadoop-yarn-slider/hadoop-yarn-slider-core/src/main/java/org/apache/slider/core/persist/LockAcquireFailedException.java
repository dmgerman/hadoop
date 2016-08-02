begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.core.persist
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
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
name|fs
operator|.
name|Path
import|;
end_import

begin_class
DECL|class|LockAcquireFailedException
specifier|public
class|class
name|LockAcquireFailedException
extends|extends
name|Exception
block|{
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|method|LockAcquireFailedException (Path path)
specifier|public
name|LockAcquireFailedException
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|super
argument_list|(
literal|"Failed to acquire lock "
operator|+
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|LockAcquireFailedException (Path path, Throwable cause)
specifier|public
name|LockAcquireFailedException
parameter_list|(
name|Path
name|path
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
literal|"Failed to acquire lock "
operator|+
name|path
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
DECL|method|getPath ()
specifier|public
name|Path
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
block|}
end_class

end_unit

