-- Accounting.dbo.TransactionHeader definition

-- Drop table

-- DROP TABLE Accounting.dbo.TransactionHeader;

CREATE TABLE Accounting.dbo.TransactionHeader (
	Period datetime NULL,
	TransactionNumber varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	TransactionCode varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	AccountID varchar(15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[Source] varchar(15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Particulars varchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	TransactionDate datetime NULL,
	Bank varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	ReferenceNo varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Amount money NULL,
	EnteredBy varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	DateEntered datetime NULL,
	DateLastModified datetime NULL,
	UpdatedBy varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Remarks varchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL
);

-- Accounting.dbo.SupplierInfo definition

-- Drop table

-- DROP TABLE Accounting.dbo.SupplierInfo;

CREATE TABLE Accounting.dbo.SupplierInfo (
	AccountID varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CompanyName varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CompanyAddress varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	TINNo varchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	ContactPerson varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	ZipCode varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	PhoneNo varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	MobileNo varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	EmailAddress varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	FaxNo varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	TaxType varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	SupplierNature varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Notes varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Status varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL
);

-- Accounting.dbo.ParticularsAccount definition

-- Drop table

-- DROP TABLE Accounting.dbo.ParticularsAccount;

CREATE TABLE Accounting.dbo.ParticularsAccount (
	Particulars varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	AccountCode varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Amount money NULL,
	CONSTRAINT PK_ParticularsAccount PRIMARY KEY (Particulars)
);

-- Accounting.dbo.ReleasingApprovals definition

-- Drop table

-- DROP TABLE Accounting.dbo.ReleasingApprovals;

CREATE TABLE Accounting.dbo.ReleasingApprovals (
	id int IDENTITY(1,1) NOT NULL,
	MIRSID int NOT NULL,
	SignatoriesID int NOT NULL,
	CreatedAt datetime NULL,
	UpdatedAt datetime NULL
);

-- Accounting.dbo.BankAccounts definition

-- Drop table

-- DROP TABLE Accounting.dbo.BankAccounts;

CREATE TABLE Accounting.dbo.BankAccounts (
	BankAccountNumber varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	BankDescription varchar(255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	AccountCode varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL
);

-- Accounting.dbo.BalanceForward definition

-- Drop table

-- DROP TABLE Accounting.dbo.BalanceForward;

CREATE TABLE Accounting.dbo.BalanceForward (
	Period datetime NULL,
	AccountCode varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	BeginningBalance money NULL,
	Debits money NULL,
	Credit money NULL,
	EndingBalance money NULL
);

-- Accounting.dbo.TransactionDefinition definition

-- Drop table

-- DROP TABLE Accounting.dbo.TransactionDefinition;

CREATE TABLE Accounting.dbo.TransactionDefinition (
	TransactionCode varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Description varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL
);

-- Accounting.dbo.ConsumerInfo definition

-- Drop table

-- DROP TABLE Accounting.dbo.ConsumerInfo;

CREATE TABLE Accounting.dbo.ConsumerInfo (
	AccountID varchar(15) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	ConsumerName varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	ConsumerAddress varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	TINNo varchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	EmailAdd varchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	ContactNo varchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL
);

-- Accounting.dbo.Periods definition

-- Drop table

-- DROP TABLE Accounting.dbo.Periods;

CREATE TABLE Accounting.dbo.Periods (
	Period datetime NOT NULL,
	Status varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	LockedBy varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	DateLocked datetime NULL,
	UnlockedBy varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	DateUnlocked datetime NULL,
	CONSTRAINT PK_Periods PRIMARY KEY (Period)
);

-- Accounting.dbo.ChartOfAccounts definition

-- Drop table

-- DROP TABLE Accounting.dbo.ChartOfAccounts;

CREATE TABLE Accounting.dbo.ChartOfAccounts (
	AccountCode varchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	Description varchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	AccountType varchar(1) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	LevelNumber smallint NULL,
	SummaryAccount varchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	GLSL char(1) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	OldAccountCode varchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	NewDesciption varchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	OldSummaryAccount varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Status varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CONSTRAINT PK_ChartOfAccounts PRIMARY KEY (AccountCode)
);

-- Accounting.dbo.TransactionDetails definition

-- Drop table

-- DROP TABLE Accounting.dbo.TransactionDetails;

CREATE TABLE Accounting.dbo.TransactionDetails (
	Period datetime NULL,
	TransactionNumber varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	TransactionCode varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	TransactionDate datetime NULL,
	SequenceNumber numeric(18,0) NULL,
	AccountCode varchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Debit money NULL,
	Credit money NULL,
	ORDateFrom date NULL,
	ORDateTo date NULL,
	BankID varchar(10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CheckNo varchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Note varchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL
);

-- Accounting.dbo.Departments definition

-- Drop table

-- DROP TABLE Accounting.dbo.Departments;

CREATE TABLE Accounting.dbo.Departments (
	DepartmentID int IDENTITY(1,1) NOT NULL,
	DepartmentName nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	DepartmentHead int NULL,
	CONSTRAINT PK_Departments PRIMARY KEY (DepartmentID)
);

-- Accounting.dbo.EmployeeInfo definition

-- Drop table

-- DROP TABLE Accounting.dbo.EmployeeInfo;

CREATE TABLE Accounting.dbo.EmployeeInfo (
	EmployeeFirstName varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	EmployeeMidName varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	EmployeeLastName varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Address varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Designation varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	DepartmentId int NOT NULL,
	EmployeeID int IDENTITY(1,1) NOT NULL,
	Phone nvarchar(15) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CONSTRAINT PK_EmployeeInfo PRIMARY KEY (EmployeeID)
);


-- Accounting.dbo.EmployeeInfo foreign keys

ALTER TABLE Accounting.dbo.EmployeeInfo ADD CONSTRAINT FK_EmployeeInfo_Department FOREIGN KEY (DepartmentId) REFERENCES Accounting.dbo.Departments(DepartmentID);

-- Accounting.dbo.StockTypes definition

-- Drop table

-- DROP TABLE Accounting.dbo.StockTypes;

CREATE TABLE Accounting.dbo.StockTypes (
	id int IDENTITY(1,1) NOT NULL,
	StockType nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	Units nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	CONSTRAINT PK_StockTypes PRIMARY KEY (id)
);

-- Accounting.dbo.users definition

-- Drop table

-- DROP TABLE Accounting.dbo.users;

CREATE TABLE Accounting.dbo.users (
	id int IDENTITY(1,1) NOT NULL,
	username nvarchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	fullname nvarchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	password nvarchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	EmployeeID int NOT NULL,
	CONSTRAINT PK__users__3213E83F8688271C PRIMARY KEY (id),
	CONSTRAINT UQ__users__F3DBC572DCF6873E UNIQUE (username)
);

ALTER TABLE Accounting.dbo.USERS ADD CONSTRAINT FK_user_employee_info FOREIGN KEY (EmployeeID) REFERENCES Accounting.dbo.EmployeeInfo(EmployeeID);

-- Accounting.dbo.permissions definition

-- Drop table

-- DROP TABLE Accounting.dbo.permissions;

CREATE TABLE Accounting.dbo.permissions (
	id int IDENTITY(1,1) NOT NULL,
	permission nvarchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	remarks nvarchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CONSTRAINT PK__permissi__3213E83F06994590 PRIMARY KEY (id)
);

-- Accounting.dbo.user_permissions definition

-- Drop table

-- DROP TABLE Accounting.dbo.user_permissions;

CREATE TABLE Accounting.dbo.user_permissions (
	user_id int NOT NULL,
	permission_id int NOT NULL,
	CONSTRAINT PK_UserPermissions PRIMARY KEY (user_id,permission_id)
);


-- Accounting.dbo.user_permissions foreign keys

ALTER TABLE Accounting.dbo.user_permissions ADD CONSTRAINT FK_Permission_UserPermissions FOREIGN KEY (permission_id) REFERENCES Accounting.dbo.permissions(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE Accounting.dbo.user_permissions ADD CONSTRAINT FK_User_UserPermissions FOREIGN KEY (user_id) REFERENCES Accounting.dbo.users(id) ON DELETE CASCADE ON UPDATE CASCADE;

-- Accounting.dbo.roles definition

-- Drop table

-- DROP TABLE Accounting.dbo.roles;

CREATE TABLE Accounting.dbo.roles (
	id int IDENTITY(1,1) NOT NULL,
	[role] nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	description nvarchar(191) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CONSTRAINT PK_roles PRIMARY KEY (id)
);

-- Accounting.dbo.user_roles definition

-- Drop table

-- DROP TABLE Accounting.dbo.user_roles;

CREATE TABLE Accounting.dbo.user_roles (
	user_id int NOT NULL,
	role_id int NOT NULL,
	CONSTRAINT PK_user_roles PRIMARY KEY (user_id,role_id)
);


-- Accounting.dbo.user_roles foreign keys

ALTER TABLE Accounting.dbo.user_roles ADD CONSTRAINT FK_user_roles_role FOREIGN KEY (role_id) REFERENCES Accounting.dbo.roles(id);
ALTER TABLE Accounting.dbo.user_roles ADD CONSTRAINT FK_user_roles_user FOREIGN KEY (user_id) REFERENCES Accounting.dbo.users(id);

-- Accounting.dbo.role_permissions definition

-- Drop table

-- DROP TABLE Accounting.dbo.role_permissions;

CREATE TABLE Accounting.dbo.role_permissions (
	role_id int NOT NULL,
	permission_id int NOT NULL,
	CONSTRAINT PK_role_permissions PRIMARY KEY (role_id,permission_id)
);


-- Accounting.dbo.role_permissions foreign keys

ALTER TABLE Accounting.dbo.role_permissions ADD CONSTRAINT FK_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES Accounting.dbo.permissions(id);
ALTER TABLE Accounting.dbo.role_permissions ADD CONSTRAINT FK_role_permissions_role FOREIGN KEY (role_id) REFERENCES Accounting.dbo.roles(id);

-- Accounting.dbo.Signatories definition

-- Drop table

-- DROP TABLE Accounting.dbo.Signatories;

CREATE TABLE Accounting.dbo.Signatories (
	id int IDENTITY(1,1) NOT NULL,
	[Type] nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	UserID int NOT NULL,
	[Rank] smallint NOT NULL,
	Comments nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CreatedAt datetime NULL,
	UpdatedAt datetime NULL,
	CONSTRAINT PK_Signatories PRIMARY KEY (id)
);


-- Accounting.dbo.Signatories foreign keys

ALTER TABLE Accounting.dbo.Signatories ADD CONSTRAINT FK_Signatories_User FOREIGN KEY (UserID) REFERENCES Accounting.dbo.users(id);

-- Accounting.dbo.Stocks definition

-- Drop table

-- DROP TABLE Accounting.dbo.Stocks;

CREATE TABLE Accounting.dbo.Stocks (
	id int IDENTITY(1,1) NOT NULL,
	StockName nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Description nvarchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	SerialNumber nvarchar(100)  COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Brand nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Model nvarchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	ManufacturingDate date NULL,
	ValidityDate date NULL,
	TypeID int NULL,
	Unit nvarchar(30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Quantity int NULL,
    Critical int NULL,
	Price money NULL,
	NEACode nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	IsTrashed bit DEFAULT 0 NULL,
	Comments nvarchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CreatedAt datetime NULL,
	UserIDCreated int NULL,
	UpdatedAt datetime NULL,
	UserIDUpdated int NULL,
	TrashedAt datetime NULL,
	UserIDTrashed int NULL,
	CONSTRAINT PK_Stocks PRIMARY KEY (id)
);


-- Accounting.dbo.Stocks foreign keys

ALTER TABLE Accounting.dbo.Stocks ADD CONSTRAINT FK_Stocks_Creator FOREIGN KEY (UserIDCreated) REFERENCES Accounting.dbo.users(id);
ALTER TABLE Accounting.dbo.Stocks ADD CONSTRAINT FK_Stocks_StockType FOREIGN KEY (TypeID) REFERENCES Accounting.dbo.StockTypes(id);
ALTER TABLE Accounting.dbo.Stocks ADD CONSTRAINT FK_Stocks_Trasher FOREIGN KEY (UserIDTrashed) REFERENCES Accounting.dbo.users(id);
ALTER TABLE Accounting.dbo.Stocks ADD CONSTRAINT FK_Stocks_Updator FOREIGN KEY (UserIDUpdated) REFERENCES Accounting.dbo.users(id);

-- Accounting.dbo.MIRS definition

-- Drop table

-- DROP TABLE Accounting.dbo.MIRS;

CREATE TABLE Accounting.dbo.MIRS (
	id int NOT NULL,
	DateFiled datetime NULL,
	Purpose nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Details nvarchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Status nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	RequisitionerID int NULL,
	UserID int NULL,
	CreatedAt datetime NULL,
	UpdatedAt datetime NULL,
	CONSTRAINT PK_MIRS PRIMARY KEY (id)
);


-- Accounting.dbo.MIRS foreign keys

ALTER TABLE Accounting.dbo.MIRS ADD CONSTRAINT FK_MIRS_Requisitioner FOREIGN KEY (RequisitionerID) REFERENCES Accounting.dbo.EmployeeInfo(EmployeeID);
ALTER TABLE Accounting.dbo.MIRS ADD CONSTRAINT FK_MIRS_User FOREIGN KEY (UserID) REFERENCES Accounting.dbo.users(id);

-- Accounting.dbo.Releasing definition

-- Drop table

-- DROP TABLE Accounting.dbo.Releasing;

CREATE TABLE Accounting.dbo.Releasing (
	id int IDENTITY(1,1) NOT NULL,
	StockID int NOT NULL,
	MIRSID int NOT NULL,
	Quantity int NOT NULL,
	Price money NOT NULL,
	UserID int NOT NULL,
	Status nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CreatedAt datetime NULL,
	UpdatedAt datetime NULL,
	CONSTRAINT PK_Releasing PRIMARY KEY (id)
);


-- Accounting.dbo.Releasing foreign keys

ALTER TABLE Accounting.dbo.Releasing ADD CONSTRAINT FK_Releasing_MIRS FOREIGN KEY (MIRSID) REFERENCES Accounting.dbo.MIRS(id);
ALTER TABLE Accounting.dbo.Releasing ADD CONSTRAINT FK_Releasing_Stock FOREIGN KEY (StockID) REFERENCES Accounting.dbo.Stocks(id);
ALTER TABLE Accounting.dbo.Releasing ADD CONSTRAINT FK_Releasing_User FOREIGN KEY (UserID) REFERENCES Accounting.dbo.users(id);

-- Accounting.dbo.MIRSItems definition

-- Drop table

-- DROP TABLE Accounting.dbo.MIRSItems;

CREATE TABLE Accounting.dbo.MIRSItems (
	id int IDENTITY(1,1) NOT NULL,
	MIRSID int NOT NULL,
	StockID int NOT NULL,
	Quantity int NOT NULL,
	Price money NOT NULL,
	Comments nvarchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CreatedAt datetime NULL,
	UpdatedAt datetime NULL
);


-- Accounting.dbo.MIRSItems foreign keys

ALTER TABLE Accounting.dbo.MIRSItems ADD CONSTRAINT FK_MIRSItems_MIRS FOREIGN KEY (MIRSID) REFERENCES Accounting.dbo.MIRS(id);
ALTER TABLE Accounting.dbo.MIRSItems ADD CONSTRAINT FK_MIRSItems_Stock FOREIGN KEY (StockID) REFERENCES Accounting.dbo.Stocks(id);

-- Accounting.dbo.MIRSSignatories definition

-- Drop table

-- DROP TABLE Accounting.dbo.MIRSSignatories;

CREATE TABLE Accounting.dbo.MIRSSignatories (
	id int IDENTITY(1,1) NOT NULL,
	MIRSID int NOT NULL,
	user_id int NOT NULL,
	Status nvarchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	Comments nvarchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	CreatedAt datetime NULL,
	UpdatedAt datetime NULL,
	CONSTRAINT PK_MIRSSignatories PRIMARY KEY (id)
);


-- Accounting.dbo.MIRSSignatories foreign keys

ALTER TABLE Accounting.dbo.MIRSSignatories ADD CONSTRAINT FK_MIRSSignatories_MIRS FOREIGN KEY (MIRSID) REFERENCES Accounting.dbo.MIRS(id);
ALTER TABLE Accounting.dbo.MIRSSignatories ADD CONSTRAINT FK_MIRSSignatories_Signatories FOREIGN KEY (user_id) REFERENCES Accounting.dbo.users(id);
