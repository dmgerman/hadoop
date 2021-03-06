begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * A test class for AutoCloseableLock.  */
end_comment

begin_class
DECL|class|TestAutoCloseableLock
specifier|public
class|class
name|TestAutoCloseableLock
block|{
comment|/**    * Test the basic lock and unlock operation.    */
annotation|@
name|Test
DECL|method|testLockAcquireRelease ()
specifier|public
name|void
name|testLockAcquireRelease
parameter_list|()
block|{
name|AutoCloseableLock
name|lock
init|=
operator|new
name|AutoCloseableLock
argument_list|()
decl_stmt|;
name|AutoCloseableLock
name|newlock
init|=
name|lock
operator|.
name|acquire
argument_list|()
decl_stmt|;
comment|// Ensure acquire the same lock object.
name|assertEquals
argument_list|(
name|newlock
argument_list|,
name|lock
argument_list|)
expr_stmt|;
comment|// Ensure it locked now.
name|assertTrue
argument_list|(
name|lock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
comment|// Ensure it is unlocked now.
name|assertFalse
argument_list|(
name|lock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test when lock is acquired, no other thread can    * lock it.    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testMultipleThread ()
specifier|public
name|void
name|testMultipleThread
parameter_list|()
throws|throws
name|Exception
block|{
name|AutoCloseableLock
name|lock
init|=
operator|new
name|AutoCloseableLock
argument_list|()
decl_stmt|;
name|lock
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|lock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
name|competingThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|lock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|lock
operator|.
name|tryLock
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|competingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|competingThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|lock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
name|lock
operator|.
name|release
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|lock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the correctness under try-with-resource syntax.    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testTryWithResourceSyntax ()
specifier|public
name|void
name|testTryWithResourceSyntax
parameter_list|()
throws|throws
name|Exception
block|{
name|AutoCloseableLock
name|lock
init|=
operator|new
name|AutoCloseableLock
argument_list|()
decl_stmt|;
try|try
init|(
name|AutoCloseableLock
name|localLock
init|=
name|lock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|assertEquals
argument_list|(
name|localLock
argument_list|,
name|lock
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|lock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
name|competingThread
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|lock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|lock
operator|.
name|tryLock
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|competingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|competingThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|localLock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|lock
operator|.
name|isLocked
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

