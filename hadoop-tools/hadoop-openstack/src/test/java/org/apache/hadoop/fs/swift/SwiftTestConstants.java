begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift
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
package|;
end_package

begin_comment
comment|/**  * Hard coded constants for the test timeouts  */
end_comment

begin_interface
DECL|interface|SwiftTestConstants
specifier|public
interface|interface
name|SwiftTestConstants
block|{
comment|/**    * Timeout for swift tests: {@value}    */
DECL|field|SWIFT_TEST_TIMEOUT
name|int
name|SWIFT_TEST_TIMEOUT
init|=
literal|5
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|/**    * Timeout for tests performing bulk operations: {@value}    */
DECL|field|SWIFT_BULK_IO_TEST_TIMEOUT
name|int
name|SWIFT_BULK_IO_TEST_TIMEOUT
init|=
literal|12
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
block|}
end_interface

end_unit

