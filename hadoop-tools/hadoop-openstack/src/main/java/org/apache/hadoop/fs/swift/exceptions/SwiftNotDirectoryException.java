begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|exceptions
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

begin_comment
comment|/**  * Exception raised when an operation is meant to work on a directory, but  * the target path is not a directory  */
end_comment

begin_class
DECL|class|SwiftNotDirectoryException
specifier|public
class|class
name|SwiftNotDirectoryException
extends|extends
name|SwiftException
block|{
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|method|SwiftNotDirectoryException (Path path)
specifier|public
name|SwiftNotDirectoryException
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|SwiftNotDirectoryException (Path path, String message)
specifier|public
name|SwiftNotDirectoryException
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|path
operator|.
name|toString
argument_list|()
operator|+
name|message
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

