begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.permission
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * Tests covering basic functionality of the ACL objects.  */
end_comment

begin_class
DECL|class|TestAcl
specifier|public
class|class
name|TestAcl
block|{
DECL|field|ENTRY1
DECL|field|ENTRY2
DECL|field|ENTRY3
DECL|field|ENTRY4
DECL|field|ENTRY5
DECL|field|ENTRY6
specifier|private
specifier|static
name|AclEntry
name|ENTRY1
decl_stmt|,
name|ENTRY2
decl_stmt|,
name|ENTRY3
decl_stmt|,
name|ENTRY4
decl_stmt|,
name|ENTRY5
decl_stmt|,
name|ENTRY6
decl_stmt|,
DECL|field|ENTRY7
DECL|field|ENTRY8
DECL|field|ENTRY9
DECL|field|ENTRY10
DECL|field|ENTRY11
DECL|field|ENTRY12
DECL|field|ENTRY13
name|ENTRY7
decl_stmt|,
name|ENTRY8
decl_stmt|,
name|ENTRY9
decl_stmt|,
name|ENTRY10
decl_stmt|,
name|ENTRY11
decl_stmt|,
name|ENTRY12
decl_stmt|,
name|ENTRY13
decl_stmt|;
DECL|field|STATUS1
DECL|field|STATUS2
DECL|field|STATUS3
DECL|field|STATUS4
specifier|private
specifier|static
name|AclStatus
name|STATUS1
decl_stmt|,
name|STATUS2
decl_stmt|,
name|STATUS3
decl_stmt|,
name|STATUS4
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setUp ()
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
block|{
comment|// named user
name|AclEntry
operator|.
name|Builder
name|aclEntryBuilder
init|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|USER
argument_list|)
operator|.
name|setName
argument_list|(
literal|"user1"
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|ENTRY1
operator|=
name|aclEntryBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|ENTRY2
operator|=
name|aclEntryBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// named group
name|ENTRY3
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|GROUP
argument_list|)
operator|.
name|setName
argument_list|(
literal|"group2"
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|READ_WRITE
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// default other
name|ENTRY4
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|OTHER
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|NONE
argument_list|)
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|DEFAULT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// owner
name|ENTRY5
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|USER
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// default named group
name|ENTRY6
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|GROUP
argument_list|)
operator|.
name|setName
argument_list|(
literal|"group3"
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|READ_WRITE
argument_list|)
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|DEFAULT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// other
name|ENTRY7
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|OTHER
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|NONE
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// default named user
name|ENTRY8
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|USER
argument_list|)
operator|.
name|setName
argument_list|(
literal|"user3"
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|)
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|DEFAULT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// mask
name|ENTRY9
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|MASK
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|READ
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// default mask
name|ENTRY10
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|MASK
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|READ_EXECUTE
argument_list|)
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|DEFAULT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// group
name|ENTRY11
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|GROUP
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|READ
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// default group
name|ENTRY12
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|GROUP
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|READ
argument_list|)
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|DEFAULT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// default owner
name|ENTRY13
operator|=
operator|new
name|AclEntry
operator|.
name|Builder
argument_list|()
operator|.
name|setType
argument_list|(
name|AclEntryType
operator|.
name|USER
argument_list|)
operator|.
name|setPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|)
operator|.
name|setScope
argument_list|(
name|AclEntryScope
operator|.
name|DEFAULT
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|AclStatus
operator|.
name|Builder
name|aclStatusBuilder
init|=
operator|new
name|AclStatus
operator|.
name|Builder
argument_list|()
operator|.
name|owner
argument_list|(
literal|"owner1"
argument_list|)
operator|.
name|group
argument_list|(
literal|"group1"
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY1
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY3
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY4
argument_list|)
decl_stmt|;
name|STATUS1
operator|=
name|aclStatusBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|STATUS2
operator|=
name|aclStatusBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|STATUS3
operator|=
operator|new
name|AclStatus
operator|.
name|Builder
argument_list|()
operator|.
name|owner
argument_list|(
literal|"owner2"
argument_list|)
operator|.
name|group
argument_list|(
literal|"group2"
argument_list|)
operator|.
name|stickyBit
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|STATUS4
operator|=
operator|new
name|AclStatus
operator|.
name|Builder
argument_list|()
operator|.
name|addEntry
argument_list|(
name|ENTRY1
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY3
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY4
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY5
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY6
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY7
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY8
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY9
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY10
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY11
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY12
argument_list|)
operator|.
name|addEntry
argument_list|(
name|ENTRY13
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEntryEquals ()
specifier|public
name|void
name|testEntryEquals
parameter_list|()
block|{
name|assertNotSame
argument_list|(
name|ENTRY1
argument_list|,
name|ENTRY2
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|ENTRY1
argument_list|,
name|ENTRY3
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|ENTRY1
argument_list|,
name|ENTRY4
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|ENTRY2
argument_list|,
name|ENTRY3
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|ENTRY2
argument_list|,
name|ENTRY4
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|ENTRY3
argument_list|,
name|ENTRY4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ENTRY1
argument_list|,
name|ENTRY1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ENTRY2
argument_list|,
name|ENTRY2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ENTRY1
argument_list|,
name|ENTRY2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ENTRY2
argument_list|,
name|ENTRY1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ENTRY1
operator|.
name|equals
argument_list|(
name|ENTRY3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ENTRY1
operator|.
name|equals
argument_list|(
name|ENTRY4
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ENTRY3
operator|.
name|equals
argument_list|(
name|ENTRY4
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ENTRY1
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ENTRY1
operator|.
name|equals
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEntryHashCode ()
specifier|public
name|void
name|testEntryHashCode
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|ENTRY1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|ENTRY2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ENTRY1
operator|.
name|hashCode
argument_list|()
operator|==
name|ENTRY3
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ENTRY1
operator|.
name|hashCode
argument_list|()
operator|==
name|ENTRY4
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|ENTRY3
operator|.
name|hashCode
argument_list|()
operator|==
name|ENTRY4
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEntryNaturalOrdering ()
specifier|public
name|void
name|testEntryNaturalOrdering
parameter_list|()
block|{
name|AclEntry
name|expected
index|[]
init|=
operator|new
name|AclEntry
index|[]
block|{
name|ENTRY5
block|,
comment|// owner
name|ENTRY1
block|,
comment|// named user
name|ENTRY11
block|,
comment|// group
name|ENTRY3
block|,
comment|// named group
name|ENTRY9
block|,
comment|// mask
name|ENTRY7
block|,
comment|// other
name|ENTRY13
block|,
comment|// default owner
name|ENTRY8
block|,
comment|// default named user
name|ENTRY12
block|,
comment|// default group
name|ENTRY6
block|,
comment|// default named group
name|ENTRY10
block|,
comment|// default mask
name|ENTRY4
comment|// default other
block|}
decl_stmt|;
name|List
argument_list|<
name|AclEntry
argument_list|>
name|actual
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|STATUS4
operator|.
name|getEntries
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|actual
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|length
argument_list|,
name|actual
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expected
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|AclEntry
name|expectedEntry
init|=
name|expected
index|[
name|i
index|]
decl_stmt|;
name|AclEntry
name|actualEntry
init|=
name|actual
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"At position %d, expected = %s, actual = %s"
argument_list|,
name|i
argument_list|,
name|expectedEntry
argument_list|,
name|actualEntry
argument_list|)
argument_list|,
name|expectedEntry
argument_list|,
name|actualEntry
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testEntryScopeIsAccessIfUnspecified ()
specifier|public
name|void
name|testEntryScopeIsAccessIfUnspecified
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|,
name|ENTRY1
operator|.
name|getScope
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|,
name|ENTRY2
operator|.
name|getScope
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AclEntryScope
operator|.
name|ACCESS
argument_list|,
name|ENTRY3
operator|.
name|getScope
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AclEntryScope
operator|.
name|DEFAULT
argument_list|,
name|ENTRY4
operator|.
name|getScope
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatusEquals ()
specifier|public
name|void
name|testStatusEquals
parameter_list|()
block|{
name|assertNotSame
argument_list|(
name|STATUS1
argument_list|,
name|STATUS2
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|STATUS1
argument_list|,
name|STATUS3
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|STATUS2
argument_list|,
name|STATUS3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATUS1
argument_list|,
name|STATUS1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATUS2
argument_list|,
name|STATUS2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATUS1
argument_list|,
name|STATUS2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATUS2
argument_list|,
name|STATUS1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|STATUS1
operator|.
name|equals
argument_list|(
name|STATUS3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|STATUS2
operator|.
name|equals
argument_list|(
name|STATUS3
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|STATUS1
operator|.
name|equals
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|STATUS1
operator|.
name|equals
argument_list|(
operator|new
name|Object
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatusHashCode ()
specifier|public
name|void
name|testStatusHashCode
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|STATUS1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|STATUS2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|STATUS1
operator|.
name|hashCode
argument_list|()
operator|==
name|STATUS3
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToString ()
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"user:user1:rwx"
argument_list|,
name|ENTRY1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"user:user1:rwx"
argument_list|,
name|ENTRY2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"group:group2:rw-"
argument_list|,
name|ENTRY3
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"default:other::---"
argument_list|,
name|ENTRY4
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"owner: owner1, group: group1, acl: {entries: [user:user1:rwx, group:group2:rw-, default:other::---], stickyBit: false}"
argument_list|,
name|STATUS1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"owner: owner1, group: group1, acl: {entries: [user:user1:rwx, group:group2:rw-, default:other::---], stickyBit: false}"
argument_list|,
name|STATUS2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"owner: owner2, group: group2, acl: {entries: [], stickyBit: true}"
argument_list|,
name|STATUS3
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

