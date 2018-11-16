begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_comment
comment|/**  * A generic lease management API which can be used if a service  * needs any kind of lease management.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.lease
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|lease
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Test class to check functionality and consistency of LeaseManager.  */
end_comment

begin_class
DECL|class|TestLeaseManager
specifier|public
class|class
name|TestLeaseManager
block|{
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
comment|/**    * Dummy resource on which leases can be acquired.    */
DECL|class|DummyResource
specifier|private
specifier|static
specifier|final
class|class
name|DummyResource
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|DummyResource (String name)
specifier|private
name|DummyResource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|DummyResource
condition|)
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|DummyResource
operator|)
name|obj
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Adding to String method to fix the ErrorProne warning that this method      * is later used in String functions, which would print out (e.g.      * `org.apache.hadoop.ozone.lease.TestLeaseManager.DummyResource@      * 4488aabb`) instead of useful information.      *      * @return Name of the Dummy object.      */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"DummyResource{"
operator|+
literal|"name='"
operator|+
name|name
operator|+
literal|'\''
operator|+
literal|'}'
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testLeaseAcquireAndRelease ()
specifier|public
name|void
name|testLeaseAcquireAndRelease
parameter_list|()
throws|throws
name|LeaseException
block|{
comment|//It is assumed that the test case execution won't take more than 5 seconds,
comment|//if it takes more time increase the defaultTimeout value of LeaseManager.
name|LeaseManager
argument_list|<
name|DummyResource
argument_list|>
name|manager
init|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"Test"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyResource
name|resourceOne
init|=
operator|new
name|DummyResource
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceTwo
init|=
operator|new
name|DummyResource
argument_list|(
literal|"two"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceThree
init|=
operator|new
name|DummyResource
argument_list|(
literal|"three"
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseOne
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseTwo
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceTwo
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseThree
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceThree
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseOne
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseTwo
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceTwo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseThree
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceThree
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseTwo
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseThree
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
comment|//The below releases should not throw LeaseNotFoundException.
name|manager
operator|.
name|release
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceTwo
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceThree
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseTwo
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseThree
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLeaseAlreadyExist ()
specifier|public
name|void
name|testLeaseAlreadyExist
parameter_list|()
throws|throws
name|LeaseException
block|{
name|LeaseManager
argument_list|<
name|DummyResource
argument_list|>
name|manager
init|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"Test"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyResource
name|resourceOne
init|=
operator|new
name|DummyResource
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceTwo
init|=
operator|new
name|DummyResource
argument_list|(
literal|"two"
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseOne
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseTwo
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceTwo
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseOne
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseTwo
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceTwo
argument_list|)
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|LeaseAlreadyExistException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Resource: "
operator|+
name|resourceOne
argument_list|)
expr_stmt|;
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceTwo
argument_list|)
expr_stmt|;
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLeaseNotFound ()
specifier|public
name|void
name|testLeaseNotFound
parameter_list|()
throws|throws
name|LeaseException
throws|,
name|InterruptedException
block|{
name|LeaseManager
argument_list|<
name|DummyResource
argument_list|>
name|manager
init|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"Test"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyResource
name|resourceOne
init|=
operator|new
name|DummyResource
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceTwo
init|=
operator|new
name|DummyResource
argument_list|(
literal|"two"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceThree
init|=
operator|new
name|DummyResource
argument_list|(
literal|"three"
argument_list|)
decl_stmt|;
comment|//Case 1: lease was never acquired.
name|exception
operator|.
name|expect
argument_list|(
name|LeaseNotFoundException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Resource: "
operator|+
name|resourceOne
argument_list|)
expr_stmt|;
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
comment|//Case 2: lease is acquired and released.
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseTwo
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceTwo
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseTwo
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceTwo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseTwo
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceTwo
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseTwo
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|LeaseNotFoundException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Resource: "
operator|+
name|resourceTwo
argument_list|)
expr_stmt|;
name|manager
operator|.
name|get
argument_list|(
name|resourceTwo
argument_list|)
expr_stmt|;
comment|//Case 3: lease acquired and timed out.
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseThree
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceThree
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseThree
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceThree
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseThree
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|sleepTime
init|=
name|leaseThree
operator|.
name|getRemainingTime
argument_list|()
operator|+
literal|1000
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|//even in case of interrupt we have to wait till lease times out.
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseThree
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|LeaseNotFoundException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Resource: "
operator|+
name|resourceThree
argument_list|)
expr_stmt|;
name|manager
operator|.
name|get
argument_list|(
name|resourceThree
argument_list|)
expr_stmt|;
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCustomLeaseTimeout ()
specifier|public
name|void
name|testCustomLeaseTimeout
parameter_list|()
throws|throws
name|LeaseException
block|{
name|LeaseManager
argument_list|<
name|DummyResource
argument_list|>
name|manager
init|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"Test"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyResource
name|resourceOne
init|=
operator|new
name|DummyResource
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceTwo
init|=
operator|new
name|DummyResource
argument_list|(
literal|"two"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceThree
init|=
operator|new
name|DummyResource
argument_list|(
literal|"three"
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseOne
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseTwo
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceTwo
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseThree
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceThree
argument_list|,
literal|50000
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseOne
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseTwo
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceTwo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseThree
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceThree
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseTwo
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseThree
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5000
argument_list|,
name|leaseOne
operator|.
name|getLeaseLifeTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|10000
argument_list|,
name|leaseTwo
operator|.
name|getLeaseLifeTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|50000
argument_list|,
name|leaseThree
operator|.
name|getLeaseLifeTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// Releasing of leases is done in shutdown, so don't have to worry about
comment|// lease release
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLeaseCallback ()
specifier|public
name|void
name|testLeaseCallback
parameter_list|()
throws|throws
name|LeaseException
throws|,
name|InterruptedException
block|{
name|Map
argument_list|<
name|DummyResource
argument_list|,
name|String
argument_list|>
name|leaseStatus
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|LeaseManager
argument_list|<
name|DummyResource
argument_list|>
name|manager
init|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"Test"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyResource
name|resourceOne
init|=
operator|new
name|DummyResource
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseOne
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceOne
argument_list|,
literal|"lease in use"
argument_list|)
expr_stmt|;
name|leaseOne
operator|.
name|registerCallBack
argument_list|(
parameter_list|()
lambda|->
block|{
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceOne
argument_list|,
literal|"lease expired"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
comment|// wait for lease to expire
name|long
name|sleepTime
init|=
name|leaseOne
operator|.
name|getRemainingTime
argument_list|()
operator|+
literal|1000
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|//even in case of interrupt we have to wait till lease times out.
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|LeaseNotFoundException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Resource: "
operator|+
name|resourceOne
argument_list|)
expr_stmt|;
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
comment|// check if callback has been executed
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"lease expired"
argument_list|,
name|leaseStatus
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCallbackExecutionInCaseOfLeaseRelease ()
specifier|public
name|void
name|testCallbackExecutionInCaseOfLeaseRelease
parameter_list|()
throws|throws
name|LeaseException
throws|,
name|InterruptedException
block|{
comment|// Callbacks should not be executed in case of lease release
name|Map
argument_list|<
name|DummyResource
argument_list|,
name|String
argument_list|>
name|leaseStatus
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|LeaseManager
argument_list|<
name|DummyResource
argument_list|>
name|manager
init|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"Test"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyResource
name|resourceOne
init|=
operator|new
name|DummyResource
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseOne
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceOne
argument_list|,
literal|"lease in use"
argument_list|)
expr_stmt|;
name|leaseOne
operator|.
name|registerCallBack
argument_list|(
parameter_list|()
lambda|->
block|{
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceOne
argument_list|,
literal|"lease expired"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceOne
argument_list|,
literal|"lease released"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|LeaseNotFoundException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Resource: "
operator|+
name|resourceOne
argument_list|)
expr_stmt|;
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"lease released"
argument_list|,
name|leaseStatus
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLeaseCallbackWithMultipleLeases ()
specifier|public
name|void
name|testLeaseCallbackWithMultipleLeases
parameter_list|()
throws|throws
name|LeaseException
throws|,
name|InterruptedException
block|{
name|Map
argument_list|<
name|DummyResource
argument_list|,
name|String
argument_list|>
name|leaseStatus
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|LeaseManager
argument_list|<
name|DummyResource
argument_list|>
name|manager
init|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"Test"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyResource
name|resourceOne
init|=
operator|new
name|DummyResource
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceTwo
init|=
operator|new
name|DummyResource
argument_list|(
literal|"two"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceThree
init|=
operator|new
name|DummyResource
argument_list|(
literal|"three"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceFour
init|=
operator|new
name|DummyResource
argument_list|(
literal|"four"
argument_list|)
decl_stmt|;
name|DummyResource
name|resourceFive
init|=
operator|new
name|DummyResource
argument_list|(
literal|"five"
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseOne
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseTwo
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceTwo
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseThree
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceThree
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseFour
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceFour
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseFive
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceFive
argument_list|)
decl_stmt|;
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceOne
argument_list|,
literal|"lease in use"
argument_list|)
expr_stmt|;
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceTwo
argument_list|,
literal|"lease in use"
argument_list|)
expr_stmt|;
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceThree
argument_list|,
literal|"lease in use"
argument_list|)
expr_stmt|;
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceFour
argument_list|,
literal|"lease in use"
argument_list|)
expr_stmt|;
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceFive
argument_list|,
literal|"lease in use"
argument_list|)
expr_stmt|;
name|leaseOne
operator|.
name|registerCallBack
argument_list|(
parameter_list|()
lambda|->
block|{
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceOne
argument_list|,
literal|"lease expired"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
name|leaseTwo
operator|.
name|registerCallBack
argument_list|(
parameter_list|()
lambda|->
block|{
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceTwo
argument_list|,
literal|"lease expired"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
name|leaseThree
operator|.
name|registerCallBack
argument_list|(
parameter_list|()
lambda|->
block|{
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceThree
argument_list|,
literal|"lease expired"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
name|leaseFour
operator|.
name|registerCallBack
argument_list|(
parameter_list|()
lambda|->
block|{
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceFour
argument_list|,
literal|"lease expired"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
name|leaseFive
operator|.
name|registerCallBack
argument_list|(
parameter_list|()
lambda|->
block|{
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceFive
argument_list|,
literal|"lease expired"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
comment|// release lease one, two and three
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceOne
argument_list|,
literal|"lease released"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceTwo
argument_list|,
literal|"lease released"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceTwo
argument_list|)
expr_stmt|;
name|leaseStatus
operator|.
name|put
argument_list|(
name|resourceThree
argument_list|,
literal|"lease released"
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceThree
argument_list|)
expr_stmt|;
comment|// wait for other leases to expire
name|long
name|sleepTime
init|=
name|leaseFive
operator|.
name|getRemainingTime
argument_list|()
operator|+
literal|1000
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|//even in case of interrupt we have to wait till lease times out.
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseTwo
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseThree
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseFour
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseFive
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"lease released"
argument_list|,
name|leaseStatus
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"lease released"
argument_list|,
name|leaseStatus
operator|.
name|get
argument_list|(
name|resourceTwo
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"lease released"
argument_list|,
name|leaseStatus
operator|.
name|get
argument_list|(
name|resourceThree
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"lease expired"
argument_list|,
name|leaseStatus
operator|.
name|get
argument_list|(
name|resourceFour
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"lease expired"
argument_list|,
name|leaseStatus
operator|.
name|get
argument_list|(
name|resourceFive
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReuseReleasedLease ()
specifier|public
name|void
name|testReuseReleasedLease
parameter_list|()
throws|throws
name|LeaseException
block|{
name|LeaseManager
argument_list|<
name|DummyResource
argument_list|>
name|manager
init|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"Test"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyResource
name|resourceOne
init|=
operator|new
name|DummyResource
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseOne
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseOne
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|sameResourceLease
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|sameResourceLease
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|sameResourceLease
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sameResourceLease
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReuseTimedOutLease ()
specifier|public
name|void
name|testReuseTimedOutLease
parameter_list|()
throws|throws
name|LeaseException
throws|,
name|InterruptedException
block|{
name|LeaseManager
argument_list|<
name|DummyResource
argument_list|>
name|manager
init|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"Test"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyResource
name|resourceOne
init|=
operator|new
name|DummyResource
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseOne
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseOne
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
comment|// wait for lease to expire
name|long
name|sleepTime
init|=
name|leaseOne
operator|.
name|getRemainingTime
argument_list|()
operator|+
literal|1000
decl_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ex
parameter_list|)
block|{
comment|//even in case of interrupt we have to wait till lease times out.
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTime
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|sameResourceLease
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|sameResourceLease
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|sameResourceLease
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sameResourceLease
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRenewLease ()
specifier|public
name|void
name|testRenewLease
parameter_list|()
throws|throws
name|LeaseException
throws|,
name|InterruptedException
block|{
name|LeaseManager
argument_list|<
name|DummyResource
argument_list|>
name|manager
init|=
operator|new
name|LeaseManager
argument_list|<>
argument_list|(
literal|"Test"
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
name|DummyResource
name|resourceOne
init|=
operator|new
name|DummyResource
argument_list|(
literal|"one"
argument_list|)
decl_stmt|;
name|Lease
argument_list|<
name|DummyResource
argument_list|>
name|leaseOne
init|=
name|manager
operator|.
name|acquire
argument_list|(
name|resourceOne
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseOne
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
comment|// add 5 more seconds to the lease
name|leaseOne
operator|.
name|renew
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// lease should still be active
name|Assert
operator|.
name|assertEquals
argument_list|(
name|leaseOne
argument_list|,
name|manager
operator|.
name|get
argument_list|(
name|resourceOne
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|leaseOne
operator|.
name|hasExpired
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|release
argument_list|(
name|resourceOne
argument_list|)
expr_stmt|;
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

