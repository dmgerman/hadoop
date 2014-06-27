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
name|assertEquals
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|UnknownHostException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestMachineList
specifier|public
class|class
name|TestMachineList
block|{
DECL|field|IP_LIST
specifier|private
specifier|static
name|String
name|IP_LIST
init|=
literal|"10.119.103.110,10.119.103.112,10.119.103.114"
decl_stmt|;
DECL|field|IP_LIST_SPACES
specifier|private
specifier|static
name|String
name|IP_LIST_SPACES
init|=
literal|" 10.119.103.110 , 10.119.103.112,10.119.103.114 ,10.119.103.110, "
decl_stmt|;
DECL|field|CIDR_LIST
specifier|private
specifier|static
name|String
name|CIDR_LIST
init|=
literal|"10.222.0.0/16,10.241.23.0/24"
decl_stmt|;
DECL|field|CIDR_LIST1
specifier|private
specifier|static
name|String
name|CIDR_LIST1
init|=
literal|"10.222.0.0/16"
decl_stmt|;
DECL|field|CIDR_LIST2
specifier|private
specifier|static
name|String
name|CIDR_LIST2
init|=
literal|"10.241.23.0/24"
decl_stmt|;
DECL|field|INVALID_CIDR
specifier|private
specifier|static
name|String
name|INVALID_CIDR
init|=
literal|"10.241/24"
decl_stmt|;
DECL|field|IP_CIDR_LIST
specifier|private
specifier|static
name|String
name|IP_CIDR_LIST
init|=
literal|"10.222.0.0/16,10.119.103.110,10.119.103.112,10.119.103.114,10.241.23.0/24"
decl_stmt|;
DECL|field|HOST_LIST
specifier|private
specifier|static
name|String
name|HOST_LIST
init|=
literal|"host1,host4"
decl_stmt|;
DECL|field|HOSTNAME_IP_CIDR_LIST
specifier|private
specifier|static
name|String
name|HOSTNAME_IP_CIDR_LIST
init|=
literal|"host1,10.222.0.0/16,10.119.103.110,10.119.103.112,10.119.103.114,10.241.23.0/24,host4,"
decl_stmt|;
annotation|@
name|Test
DECL|method|testWildCard ()
specifier|public
name|void
name|testWildCard
parameter_list|()
block|{
comment|//create MachineList with a list of of IPs
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
literal|"*"
argument_list|)
decl_stmt|;
comment|//test for inclusion with any IP
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.112"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIPList ()
specifier|public
name|void
name|testIPList
parameter_list|()
block|{
comment|//create MachineList with a list of of IPs
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|IP_LIST
argument_list|)
decl_stmt|;
comment|//test for inclusion with an known IP
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.112"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test for exclusion with an unknown IP
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.111"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIPListSpaces ()
specifier|public
name|void
name|testIPListSpaces
parameter_list|()
block|{
comment|//create MachineList with a ip string which has duplicate ip and spaces
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|IP_LIST_SPACES
argument_list|)
decl_stmt|;
comment|//test for inclusion with an known IP
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.112"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test for exclusion with an unknown IP
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.111"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStaticIPHostNameList ()
specifier|public
name|void
name|testStaticIPHostNameList
parameter_list|()
throws|throws
name|UnknownHostException
block|{
comment|//create MachineList with a list of of Hostnames
name|InetAddress
name|addressHost1
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.1"
argument_list|)
decl_stmt|;
name|InetAddress
name|addressHost4
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.4"
argument_list|)
decl_stmt|;
name|MachineList
operator|.
name|InetAddressFactory
name|addressFactory
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|MachineList
operator|.
name|InetAddressFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressFactory
operator|.
name|getByName
argument_list|(
literal|"host1"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|addressHost1
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressFactory
operator|.
name|getByName
argument_list|(
literal|"host4"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|addressHost4
argument_list|)
expr_stmt|;
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|StringUtils
operator|.
name|getTrimmedStringCollection
argument_list|(
name|HOST_LIST
argument_list|)
argument_list|,
name|addressFactory
argument_list|)
decl_stmt|;
comment|//test for inclusion with an known IP
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test for exclusion with an unknown IP
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"1.2.3.5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHostNames ()
specifier|public
name|void
name|testHostNames
parameter_list|()
throws|throws
name|UnknownHostException
block|{
comment|//create MachineList with a list of of Hostnames
name|InetAddress
name|addressHost1
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.1"
argument_list|)
decl_stmt|;
name|InetAddress
name|addressHost4
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.4"
argument_list|)
decl_stmt|;
name|InetAddress
name|addressMockHost4
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|InetAddress
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressMockHost4
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"differentName"
argument_list|)
expr_stmt|;
name|InetAddress
name|addressMockHost5
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|InetAddress
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressMockHost5
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"host5"
argument_list|)
expr_stmt|;
name|MachineList
operator|.
name|InetAddressFactory
name|addressFactory
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|MachineList
operator|.
name|InetAddressFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressFactory
operator|.
name|getByName
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|addressMockHost4
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressFactory
operator|.
name|getByName
argument_list|(
literal|"1.2.3.5"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|addressMockHost5
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressFactory
operator|.
name|getByName
argument_list|(
literal|"host1"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|addressHost1
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressFactory
operator|.
name|getByName
argument_list|(
literal|"host4"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|addressHost4
argument_list|)
expr_stmt|;
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|StringUtils
operator|.
name|getTrimmedStringCollection
argument_list|(
name|HOST_LIST
argument_list|)
argument_list|,
name|addressFactory
argument_list|)
decl_stmt|;
comment|//test for inclusion with an known IP
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test for exclusion with an unknown IP
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"1.2.3.5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHostNamesReverserIpMatch ()
specifier|public
name|void
name|testHostNamesReverserIpMatch
parameter_list|()
throws|throws
name|UnknownHostException
block|{
comment|//create MachineList with a list of of Hostnames
name|InetAddress
name|addressHost1
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.1"
argument_list|)
decl_stmt|;
name|InetAddress
name|addressHost4
init|=
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"1.2.3.4"
argument_list|)
decl_stmt|;
name|InetAddress
name|addressMockHost4
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|InetAddress
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressMockHost4
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"host4"
argument_list|)
expr_stmt|;
name|InetAddress
name|addressMockHost5
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|InetAddress
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressMockHost5
operator|.
name|getCanonicalHostName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"host5"
argument_list|)
expr_stmt|;
name|MachineList
operator|.
name|InetAddressFactory
name|addressFactory
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|MachineList
operator|.
name|InetAddressFactory
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressFactory
operator|.
name|getByName
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|addressMockHost4
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressFactory
operator|.
name|getByName
argument_list|(
literal|"1.2.3.5"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|addressMockHost5
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressFactory
operator|.
name|getByName
argument_list|(
literal|"host1"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|addressHost1
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|addressFactory
operator|.
name|getByName
argument_list|(
literal|"host4"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|addressHost4
argument_list|)
expr_stmt|;
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|StringUtils
operator|.
name|getTrimmedStringCollection
argument_list|(
name|HOST_LIST
argument_list|)
argument_list|,
name|addressFactory
argument_list|)
decl_stmt|;
comment|//test for inclusion with an known IP
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"1.2.3.4"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test for exclusion with an unknown IP
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"1.2.3.5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCIDRs ()
specifier|public
name|void
name|testCIDRs
parameter_list|()
block|{
comment|//create MachineList with a list of of ip ranges specified in CIDR format
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|CIDR_LIST
argument_list|)
decl_stmt|;
comment|//test for inclusion/exclusion
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.221.255.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.0.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.255.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.255.254"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.255.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.223.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.254"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.255"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test for exclusion with an unknown IP
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.111"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCIDRWith16bitmask ()
specifier|public
name|void
name|testCIDRWith16bitmask
parameter_list|()
block|{
comment|//create MachineList with a list of of ip ranges specified in CIDR format
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|CIDR_LIST1
argument_list|)
decl_stmt|;
comment|//test for inclusion/exclusion
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.221.255.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.0.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.0.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.255.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.255.254"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.255.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.223.0.0"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test for exclusion with an unknown IP
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.111"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCIDRWith8BitMask ()
specifier|public
name|void
name|testCIDRWith8BitMask
parameter_list|()
block|{
comment|//create MachineList with a list of of ip ranges specified in CIDR format
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|CIDR_LIST2
argument_list|)
decl_stmt|;
comment|//test for inclusion/exclusion
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.22.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.254"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.24.0"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test for exclusion with an unknown IP
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.111"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//test invalid cidr
annotation|@
name|Test
DECL|method|testInvalidCIDR ()
specifier|public
name|void
name|testInvalidCIDR
parameter_list|()
block|{
comment|//create MachineList with an Invalid CIDR
try|try
block|{
operator|new
name|MachineList
argument_list|(
name|INVALID_CIDR
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//expected Exception
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Expected only IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//
annotation|@
name|Test
DECL|method|testIPandCIDRs ()
specifier|public
name|void
name|testIPandCIDRs
parameter_list|()
block|{
comment|//create MachineList with a list of of ip ranges and ip addresses
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|IP_CIDR_LIST
argument_list|)
decl_stmt|;
comment|//test for inclusion with an known IP
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.112"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test for exclusion with an unknown IP
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.111"
argument_list|)
argument_list|)
expr_stmt|;
comment|//CIDR Ranges
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.221.255.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.255.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.223.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.22.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.24.0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHostNameIPandCIDRs ()
specifier|public
name|void
name|testHostNameIPandCIDRs
parameter_list|()
block|{
comment|//create MachineList with a mix of ip addresses , hostnames and ip ranges
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|HOSTNAME_IP_CIDR_LIST
argument_list|)
decl_stmt|;
comment|//test for inclusion with an known IP
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.112"
argument_list|)
argument_list|)
expr_stmt|;
comment|//test for exclusion with an unknown IP
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.119.103.111"
argument_list|)
argument_list|)
expr_stmt|;
comment|//CIDR Ranges
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.221.255.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.222.255.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.223.0.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.22.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.0"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.23.255"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ml
operator|.
name|includes
argument_list|(
literal|"10.241.24.0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetCollection ()
specifier|public
name|void
name|testGetCollection
parameter_list|()
block|{
comment|//create MachineList with a mix of ip addresses , hostnames and ip ranges
name|MachineList
name|ml
init|=
operator|new
name|MachineList
argument_list|(
name|HOSTNAME_IP_CIDR_LIST
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|col
init|=
name|ml
operator|.
name|getCollection
argument_list|()
decl_stmt|;
comment|//test getCollectionton to return the full collection
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|ml
operator|.
name|getCollection
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|item
range|:
name|StringUtils
operator|.
name|getTrimmedStringCollection
argument_list|(
name|HOSTNAME_IP_CIDR_LIST
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
name|col
operator|.
name|contains
argument_list|(
name|item
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

